package org.maxgamer.maxbans.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * provisional class, just for a small fix
 * @see https://github.com/netherfoam/MaxBans-Plus/pull/71
 */
public abstract class AbstractMaxBansEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}