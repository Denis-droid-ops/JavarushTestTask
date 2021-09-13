package com.game.service;

import com.game.BadRequestException;
import com.game.NotFoundException;
import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlayerServiceImpl implements PlayerService{
    @Autowired
    PlayerRepository playerRepository;

    public PlayerServiceImpl() {
    }

    @Override
    public Player getById(Long id) {
        if(!playerRepository.existsById(id)){
            throw new NotFoundException();
        }else
        return playerRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Player createPlayer(final Player player) {
        if(player==null ||
           player.getName()==null ||
           player.getName().isEmpty() ||
           player.getName().length()>12 ||
           player.getTitle()==null ||
           player.getTitle().isEmpty() ||
           player.getTitle().length()>30 ||
           player.getRace()==null ||
           player.getProfession()==null ||
           player.getExperience()==null ||
           player.getExperience()<0 ||
           player.getExperience()>10000000 ||
           //player.getLevel()==null ||
           //player.getUntilNextLevel()==null ||
           player.getBirthday()==null ||
           player.getBirthday().getTime()<0 ||
           player.getBirthday().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear()<2000 ||
           player.getBirthday().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear()>3000){
            throw new BadRequestException();
        }
        if(player.getBanned()==null) player.setBanned(false);
        player.setLevel((int)Math.round(computeLevel(player)));
        player.setUntilNextLevel((int)Math.round(computeUntilNextLevel(player)));

        return playerRepository.saveAndFlush(player);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if(!playerRepository.existsById(id)){
            throw new NotFoundException();
        }
        playerRepository.deleteById(id);
    }

    @Override
    public List<Player> getPlayersList(String name, String title, Race race, Profession profession,Long after,Long before,
                                      Boolean banned,Integer minExperience,Integer maxExperience, Integer minLevel, Integer maxLevel){
        List<Player> filteredPlayers = playerRepository.findAll();
        if(name!=null){
            filteredPlayers = filteredPlayers.stream().filter
                    (player -> player.getName().contains(name)).collect(Collectors.toList());
        }
        if(title!=null){
            filteredPlayers = filteredPlayers.stream().filter
                    (player -> player.getTitle().contains(title)).collect(Collectors.toList());
        }
        if(race!=null){
            filteredPlayers = filteredPlayers.stream().filter
                    (player -> player.getRace().equals(race)).collect(Collectors.toList());
        }
        if(profession!=null){
            filteredPlayers = filteredPlayers.stream().filter
                    (player -> player.getProfession().equals(profession)).collect(Collectors.toList());
        }
        if(after!=null){
            filteredPlayers = filteredPlayers.stream().filter
                    (player -> player.getBirthday().after(new Date(after))).collect(Collectors.toList());
        }
        if(before!=null){
            filteredPlayers = filteredPlayers.stream().filter
                    (player -> player.getBirthday().before(new Date(before))).collect(Collectors.toList());
        }
        if(banned!=null){
            filteredPlayers = filteredPlayers.stream().filter
                    (player -> player.getBanned().equals(banned)).collect(Collectors.toList());
        }
        if(minExperience!=null){
            filteredPlayers = filteredPlayers.stream().filter
                    (player -> player.getExperience()>=minExperience).collect(Collectors.toList());
        }
        if(maxExperience!=null){
            filteredPlayers = filteredPlayers.stream().filter
                    (player -> player.getExperience()<=maxExperience).collect(Collectors.toList());
        }
        if(minLevel!=null){
            filteredPlayers = filteredPlayers.stream().filter
                    (player -> player.getLevel()>=minLevel).collect(Collectors.toList());
        }
        if(maxLevel!=null){
            filteredPlayers = filteredPlayers.stream().filter
                    (player -> player.getLevel()<=maxLevel).collect(Collectors.toList());
        }
        return filteredPlayers;
    }

    @Override
    public List<Player> prepareFilteredPlayers(List<Player> filteredPlayers, PlayerOrder order, Integer pageNumber, Integer pageSize) {
        pageNumber = pageNumber==null ? 0 : pageNumber;
        pageSize = pageSize==null ? 3: pageSize;
        return filteredPlayers.stream().sorted(getComparator(order))
                .skip(pageNumber*pageSize).limit(pageSize).collect(Collectors.toList());
    }
    @Override
    public Comparator<Player> getComparator(PlayerOrder order) {
        if(order==null){
            return Comparator.comparing(Player::getId);
        }
        Comparator<Player> comparator = null;
        if(order.getFieldName().equals("id")){
            comparator = Comparator.comparing(Player::getId);
        }else if(order.getFieldName().equals("name")){
              comparator = Comparator.comparing(Player::getName);
        }else if(order.getFieldName().equals("experience")){
            comparator = Comparator.comparing(Player::getExperience);
        }else if(order.getFieldName().equals("birthday")){
            comparator = Comparator.comparing(Player::getBirthday);
        }else if(order.getFieldName().equals("level")){
            comparator = Comparator.comparing(Player::getLevel);
        }
        return comparator;
    }

    @Override
    public int computeLevel(Player player) {
        int level = (int)(Math.sqrt(2500+200*player.getExperience())-50)/100;
        return level;
    }

    @Override
    public int computeUntilNextLevel(Player player) {
        int untilNextLevel = 50*(player.getLevel()+1)*(player.getLevel()+2)-player.getExperience();
        return untilNextLevel;
    }

    @Override
    //@Transactional
    public Player updatePlayer(Player player, Long id) {
        if(!playerRepository.existsById(id)){
           throw new NotFoundException();
        }
        Player playerToUpdate = playerRepository.findById(id).get();
        if(player==null){
            throw new BadRequestException();
        }

        if(player.getName()!=null){
            if(player.getName().isEmpty() || player.getName().length()>12){
                throw new BadRequestException();
            }
            playerToUpdate.setName(player.getName());
        }

        if(player.getTitle()!=null){
            if(player.getTitle().isEmpty() || player.getTitle().length()>30){
                throw new BadRequestException();
            }
            playerToUpdate.setTitle(player.getTitle());
        }

        if(player.getRace()!=null){
            playerToUpdate.setRace(player.getRace());
        }

        if(player.getProfession()!=null){
            playerToUpdate.setProfession(player.getProfession());
        }

        if(player.getExperience()!=null){
            if(player.getExperience()<0 || player.getExperience()>10000000){
                throw new BadRequestException();
            }
           playerToUpdate.setExperience(player.getExperience());
        }

        if(player.getBirthday()!=null){
            if(player.getBirthday().getTime()<0 || player.getBirthday().
                    toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear()<2000 ||
                    player.getBirthday().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear()>3000){
                throw new BadRequestException();
            }
            playerToUpdate.setBirthday(player.getBirthday());
        }

        if(player.getBanned()!=null){
            playerToUpdate.setBanned(player.getBanned());
        }

       // if(player.getLevel()!=null) {
            playerToUpdate.setLevel(computeLevel(playerToUpdate));
            playerToUpdate.setUntilNextLevel(computeUntilNextLevel(playerToUpdate));


       // }


        return playerRepository.save(playerToUpdate);
    }
    @Override
    public boolean isValidId(Long id){
        if(id==null || id !=Math.floor(id)||id<=0){
            return false;
        }
        return true;
    }


}
