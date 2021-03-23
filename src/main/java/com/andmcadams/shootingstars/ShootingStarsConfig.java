/*
 * Copyright (c) 2021, Andrew McAdams
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.andmcadams.shootingstars;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(ShootingStarsPlugin.CONFIG_GROUP_KEY)
public interface ShootingStarsConfig extends Config {
    String SHOOTING_STAR_POST_ENDPOINT_KEYNAME = "post endpoint";
    String SHOOTING_STAR_GET_ENDPOINT_KEYNAME = "get endpoint";
    String SHOOTING_STAR_SHARED_KEY_KEYNAME = "password";
    String SHOOTING_STAR_SHOW_PVP_WORLDS_KEYNAME = "pvp worlds";
    String SHOOTING_STAR_SHOW_ASGARNIA_KEYNAME = "asgarnia";
    String SHOOTING_STAR_SHOW_KARAMJA_KEYNAME = "karamja";
    String SHOOTING_STAR_SHOW_FELDIP_HILLS_KEYNAME = "feldip hills";
    String SHOOTING_STAR_SHOW_FOSSIL_ISLAND_KEYNAME = "fossil island";
    String SHOOTING_STAR_SHOW_FREMENNIK_KEYNAME = "fremennik";
    String SHOOTING_STAR_SHOW_KOUREND_KEYNAME = "kourend";
    String SHOOTING_STAR_SHOW_KANDARIN_KEYNAME = "kandarin";
    String SHOOTING_STAR_SHOW_KEBOS_KEYNAME = "kebos";
    String SHOOTING_STAR_SHOW_KHARIDIAN_DESERT_KEYNAME = "desert";
    String SHOOTING_STAR_SHOW_MISTHALIN_KEYNAME = "misthalin";
    String SHOOTING_STAR_SHOW_MORYTANIA_KEYNAME = "morytania";
    String SHOOTING_STAR_SHOW_PISCATORIS_KEYNAME = "piscatoris";
    String SHOOTING_STAR_SHOW_TIRANNWN_KEYNAME = "tirannwn";
    String SHOOTING_STAR_SHOW_WILDERNESS_KEYNAME = "wilderness";

    @ConfigItem(keyName = SHOOTING_STAR_POST_ENDPOINT_KEYNAME, position = 0, name = "POST endpoint", description = "Web endpoint to post star data to")
    default String shootingStarPostEndpointConfig() {
        return "https://z9smj03u77.execute-api.us-east-1.amazonaws.com/stars";
    }

    @ConfigItem(keyName = SHOOTING_STAR_GET_ENDPOINT_KEYNAME, position = 1, name = "GET endpoint", description = "Web endpoint to get star data from")
    default String shootingStarGetEndpointConfig() {
        return "https://z9smj03u77.execute-api.us-east-1.amazonaws.com/stars";
    }

    @ConfigItem(keyName = SHOOTING_STAR_SHARED_KEY_KEYNAME, position = 2, name = "Key", description = "A keyword to use to share stars with friends. Must be 1-10 alpha characters")
    default String shootingStarSharedKeyConfig() {
        return "global";
    }

    @ConfigItem(keyName = SHOOTING_STAR_SHOW_PVP_WORLDS_KEYNAME, position = 3, name = "Show PVP worlds", description = "Show scouted PVP worlds")
    default boolean shootingStarShowPvpWorlds() {
        return true;
    }

    @ConfigItem(keyName = SHOOTING_STAR_SHOW_ASGARNIA_KEYNAME, position = 4, name = "Show Asgarnia worlds", description = "Show scouted Asgarnia worlds")
    default boolean shootingStarShowAsgarniaWorlds() {
        return true;
    }

    @ConfigItem(keyName = SHOOTING_STAR_SHOW_KARAMJA_KEYNAME, position = 5, name = "Show Karamja worlds", description = "Show scouted Crandor and Karamja worlds")
    default boolean shootingStarShowKaramjaWorlds() {
        return true;
    }

    @ConfigItem(keyName = SHOOTING_STAR_SHOW_FELDIP_HILLS_KEYNAME, position = 6, name = "Show Feldip Hills worlds", description = "Show scouted Asgarnia worlds")
    default boolean shootingStarShowFeldipWorlds() {
        return true;
    }

    @ConfigItem(keyName = SHOOTING_STAR_SHOW_FOSSIL_ISLAND_KEYNAME, position = 7, name = "Show Fossil Island worlds", description = "Show scouted Fossil Island worlds")
    default boolean shootingStarShowFossilIslandWorlds() {
        return true;
    }

    @ConfigItem(keyName = SHOOTING_STAR_SHOW_FREMENNIK_KEYNAME, position = 8, name = "Show Fremennik worlds", description = "Show scouted Fremennik worlds")
    default boolean shootingStarShowFremennikWorlds() {
        return true;
    }

    @ConfigItem(keyName = SHOOTING_STAR_SHOW_KOUREND_KEYNAME, position = 9, name = "Show Kourend worlds", description = "Show scouted Kourend worlds")
    default boolean shootingStarShowKourendWorlds() {
        return true;
    }

    @ConfigItem(keyName = SHOOTING_STAR_SHOW_KANDARIN_KEYNAME, position = 10, name = "Show Kandarin worlds", description = "Show scouted Kandarin worlds")
    default boolean shootingStarShowKandarinWorlds() {
        return true;
    }

    @ConfigItem(keyName = SHOOTING_STAR_SHOW_KEBOS_KEYNAME, position = 11, name = "Show Kebos Lowlands worlds", description = "Show scouted Kebos Lowlands worlds")
    default boolean shootingStarShowKebosWorlds() {
        return true;
    }

    @ConfigItem(keyName = SHOOTING_STAR_SHOW_KHARIDIAN_DESERT_KEYNAME, position = 12, name = "Show Desert worlds", description = "Show scouted Desert worlds")
    default boolean shootingStarShowDesertWorlds() {
        return true;
    }

    @ConfigItem(keyName = SHOOTING_STAR_SHOW_MISTHALIN_KEYNAME, position = 13, name = "Show Misthalin worlds", description = "Show scouted Misthalin worlds")
    default boolean shootingStarShowMisthalinWorlds() {
        return true;
    }

    @ConfigItem(keyName = SHOOTING_STAR_SHOW_MORYTANIA_KEYNAME, position = 14, name = "Show Morytania worlds", description = "Show scouted Morytania worlds")
    default boolean shootingStarShowMorytaniaWorlds() {
        return true;
    }

    @ConfigItem(keyName = SHOOTING_STAR_SHOW_PISCATORIS_KEYNAME, position = 15, name = "Show Piscatoris worlds", description = "Show scouted Piscatoris worlds")
    default boolean shootingStarShowPiscatorisWorlds() {
        return true;
    }

    @ConfigItem(keyName = SHOOTING_STAR_SHOW_TIRANNWN_KEYNAME, position = 16, name = "Show Tirannwn worlds", description = "Show scouted Tirannwn worlds")
    default boolean shootingStarShowTirannwnWorlds() {
        return true;
    }

    @ConfigItem(keyName = SHOOTING_STAR_SHOW_WILDERNESS_KEYNAME, position = 17, name = "Show Wilderness worlds", description = "Show scouted Wilderness worlds")
    default boolean shootingStarShowWildernessWorlds() {
        return true;
    }
}