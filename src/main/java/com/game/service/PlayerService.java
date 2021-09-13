package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;

import java.util.Comparator;
import java.util.List;

public interface PlayerService {
    Player getById(Long id);

    Player createPlayer(final Player player);

    void delete(Long id);

    List<Player> getPlayersList(String name, String title, Race race, Profession profession, Long after, Long before,
                                      Boolean banned, Integer minExperience, Integer maxExperience, Integer minLevel, Integer maxLevel);

    List<Player> prepareFilteredPlayers(final List<Player> filteredPlayers, PlayerOrder order,Integer pageNumber,
                                        Integer pageSize);
    Comparator<Player> getComparator(PlayerOrder order);

    int computeLevel(Player player);

    int computeUntilNextLevel(Player player);

    Player updatePlayer(Player player, Long id);

    boolean isValidId(Long id);



}
