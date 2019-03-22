package me.sargunvohra.svlib.capability;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import java.util.function.Predicate;
import java.util.function.Supplier;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

/**
 * This class is responsible for attaching PlayerCapabilities to players, and making sure the data
 * is always consistent, such as across dimensional travel or respawn.
 */
@Log4j2
public class PlayerCapabilityManager<Handler extends PlayerCapability> {

  private final Supplier<Capability<Handler>> capability;
  private final Predicate<EntityPlayer> filter;
  private final ResourceLocation key;
  private final SimpleChannel channel;

  public PlayerCapabilityManager(
      Supplier<Capability<Handler>> capability,
      ResourceLocation key,
      Predicate<EntityPlayer> filter,
      String protocolVersion) {
    this.capability = capability;
    this.key = key;
    this.filter = filter;
    this.channel =
        NetworkRegistry.newSimpleChannel(
            key, () -> protocolVersion, protocolVersion::equals, protocolVersion::equals);
    this.channel.registerMessage(
        0, Message.class, Message::encoder, Message::decoder, this::syncFromServer);
  }

  @SubscribeEvent
  void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
    val entity = event.getObject();
    if (!(entity instanceof EntityPlayer)) return;

    val player = (EntityPlayer) entity;
    if (!filter.test(player)) return;

    val provider = new PlayerCapabilityProvider<Handler>(capability.get(), key, this::syncToClient);
    event.addCapability(key, provider);
    provider.attach(player);
  }

  @SubscribeEvent
  void onEntityJoinWorld(EntityJoinWorldEvent event) {
    val entity = event.getEntity();
    if (!(entity instanceof EntityPlayerMP)) return;
    val player = ((EntityPlayerMP) entity);
    player.getCapability(capability.get()).ifPresent(handler -> syncToClient(player, handler));
  }

  @SubscribeEvent
  void onPlayerClone(PlayerEvent.Clone event) {
    event
        .getEntityPlayer()
        .getCapability(capability.get())
        .ifPresent(
            handler -> initializeClone(event.isWasDeath(), event.getEntityPlayer(), handler));
  }

  private void initializeClone(boolean wasDeath, EntityPlayer newPlayer, Handler newHandler) {
    if (newHandler.shouldPersist(wasDeath)) {
      val entityData = newPlayer.getEntityData();
      if (!entityData.contains(EntityPlayer.PERSISTED_NBT_TAG)) return;

      val persistedData = (NBTTagCompound) entityData.get(EntityPlayer.PERSISTED_NBT_TAG);
      val keyStr = key.toString();
      if (!persistedData.contains(keyStr)) return;

      val data = persistedData.get(keyStr);
      val cap = capability.get();
      cap.getStorage().readNBT(cap, newHandler, null, data);
    }
  }

  private void syncToClient(EntityPlayerMP player, Handler handler) {
    if (player.connection == null) return; // player doesn't have a client yet
    val cap = capability.get();
    val nbt = cap.getStorage().writeNBT(cap, handler, null);
    val message = new PlayerCapabilityManager.Message(nbt);
    channel.send(PacketDistributor.PLAYER.with(() -> player), message);
  }

  public void syncFromServer(Message message, Supplier<NetworkEvent.Context> contextSupplier) {
    val context = contextSupplier.get();
    if (context.getDirection() != NetworkDirection.PLAY_TO_CLIENT) return;
    context.setPacketHandled(true);

    context.enqueueWork(
        () -> {
          val cap = this.capability.get();
          Minecraft.getInstance()
              .player
              .getCapability(cap)
              .ifPresent(
                  handler -> {
                    cap.getStorage().readNBT(cap, handler, null, message.data);
                    LOG.info("Client updated: {}", handler);
                  });
        });
  }

  @Data
  static class Message {
    private final INBTBase data;

    @SneakyThrows
    public static void encoder(Message message, PacketBuffer buffer) {
      val data = message.getData();
      val out = new ByteBufOutputStream(buffer);
      out.writeByte(data.getId());
      message.getData().write(out);
    }

    @SneakyThrows
    public static Message decoder(PacketBuffer buffer) {
      val in = new ByteBufInputStream(buffer);
      val data = INBTBase.create(in.readByte());
      data.read(in, 0, NBTSizeTracker.INFINITE);
      return new Message(data);
    }
  }
}
