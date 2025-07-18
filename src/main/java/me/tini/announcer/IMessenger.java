package me.tini.announcer;

public interface IMessenger {

    void registerOutgoingChannel(String channel);

    boolean isAvailable();

    boolean sendMessage(String channel, byte[] payload);

}
