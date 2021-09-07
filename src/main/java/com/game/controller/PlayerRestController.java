package com.game.controller;


import com.game.BadRequestException;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController

public class PlayerRestController {

    private PlayerService playerService;

    @Autowired
    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping("/rest/players/{id}")
    public Player getPlayer(@PathVariable("id") Long id){
        if (!playerService.isValidId(id)) {
            throw new BadRequestException();
        }
        Player player = this.playerService.getById(id);

        return player;
    }

    @GetMapping("/rest/players")
    public List<Player>getPlayersList(@RequestParam(value = "name", required = false) String name,
                                      @RequestParam(value = "title", required = false) String title,
                                      @RequestParam(value = "race", required = false) Race race,
                                      @RequestParam(value = "profession", required = false) Profession profession,
                                      @RequestParam(value = "after", required = false) Long after,
                                      @RequestParam(value = "before", required = false) Long before,
                                      @RequestParam(value = "banned", required = false) Boolean banned,
                                      @RequestParam(value = "minExperience", required = false) Integer minExperience,
                                      @RequestParam(value = "maxExperience", required = false) Integer maxExperience,
                                      @RequestParam(value = "minLevel", required = false) Integer minLevel,
                                      @RequestParam(value = "maxLevel", required = false) Integer maxLevel,
                                      @RequestParam(value = "order", required = false) PlayerOrder order,
                                      @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                      @RequestParam(value = "pageSize", required = false) Integer pageSize){

        List<Player> players = this.playerService.getPlayersList(name,title,race,profession,after,before,banned,minExperience,
                maxExperience,minLevel,maxLevel);

        return playerService.prepareFilteredPlayers(players,order,pageNumber,pageSize);
    }

    @GetMapping("/rest/players/count")
    public int getPlayersCount(@RequestParam(value = "name", required = false) String name,
                                                       @RequestParam(value = "title", required = false) String title,
                                                       @RequestParam(value = "race", required = false) Race race,
                                                       @RequestParam(value = "profession", required = false) Profession profession,
                                                       @RequestParam(value = "after", required = false) Long after,
                                                       @RequestParam(value = "before", required = false) Long before,
                                                       @RequestParam(value = "banned", required = false) Boolean banned,
                                                       @RequestParam(value = "minExperience", required = false) Integer minExperience,
                                                       @RequestParam(value = "maxExperience", required = false) Integer maxExperience,
                                                       @RequestParam(value = "minLevel", required = false) Integer minLevel,
                                                       @RequestParam(value = "maxLevel", required = false) Integer maxLevel){


        List<Player> players = this.playerService.getPlayersList(name,title,race,profession,after,before,banned,minExperience,
                maxExperience,minLevel,maxLevel);

        //players = playerService.prepareFilteredPlayers(players,order,pageNumber,pageSize);

        return players.size();
    }

    @PostMapping("/rest/players")
    @ResponseBody
    public Player createPlayer(@RequestBody Player player){
        return playerService.createPlayer(player);
    }

    @PostMapping("/rest/players/{id}")
    @ResponseBody
    public Player updatePlayer(@RequestBody Player player, @PathVariable Long id){
        if (!playerService.isValidId(id)) {
            throw new BadRequestException();
        }
        return playerService.updatePlayer(player,id);
    }

    @DeleteMapping("/rest/players/{id}")
    public void deletePlayer(@PathVariable Long id){
        if (!playerService.isValidId(id)) {
            throw new BadRequestException();
        }
        playerService.delete(id);
    }
}
