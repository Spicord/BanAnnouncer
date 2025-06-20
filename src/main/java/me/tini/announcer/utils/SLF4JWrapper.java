/*
 * Copyright (C) 2019  OopsieWoopsie
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.tini.announcer.utils;

import java.util.logging.LogRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SLF4JWrapper extends java.util.logging.Logger {

    private final Logger logger;

    public SLF4JWrapper() {
        this(Logger.ROOT_LOGGER_NAME);
    }

    public SLF4JWrapper(String name) {
        super(name, null);
        this.logger = LoggerFactory.getLogger(name);
    }

    public SLF4JWrapper(Logger logger) {
        super(logger.getName(), null);
        this.logger = logger;
    }

    @Override
    public void log(LogRecord record) {
        final String message = record.getMessage();

        switch (record.getLevel().toString()) {
        case "CONFIG":
        case "FINE":
        case "FINER":
        case "FINEST":
            logger.debug(message); break;
        case "SEVERE":
            logger.error(message); break;
        case "WARNING":
            logger.warn(message); break;
        case "INFO":
        case "ALL":
        case "OFF":
        default:
            logger.info(message); break;
        }
    }

    public Logger getSLF4JLogger() {
        return logger;
    }
}
