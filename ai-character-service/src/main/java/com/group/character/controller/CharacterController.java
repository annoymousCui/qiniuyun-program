package com.group.character.controller;

import com.group.common.entity.Character;
import com.group.common.result.Result;
import com.group.character.service.CharacterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色控制器
 */
@RestController
@RequestMapping("/character")
@CrossOrigin
public class CharacterController {
    
    @Autowired
    private CharacterService characterService;
    
    /**
     * 搜索角色
     */
    @GetMapping("/search")
    public Result<List<Character>> searchCharacters(@RequestParam(required = false) String keyword,
                                                  @RequestParam(required = false) String category,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "20") int size) {
        List<Character> characters = characterService.searchCharacters(keyword, category, page, size);
        return Result.success(characters);
    }
    
    /**
     * 获取热门角色
     */
    @GetMapping("/popular")
    public Result<List<Character>> getPopularCharacters(@RequestParam(defaultValue = "10") int limit) {
        List<Character> characters = characterService.getPopularCharacters(limit);
        return Result.success(characters);
    }
    
    /**
     * 获取角色详情
     */
    @GetMapping("/{id}")
    public Result<Character> getCharacterById(@PathVariable String id) {
        Character character = characterService.getCharacterById(id);
        return Result.success(character);
    }
    
    /**
     * 创建角色
     */
    @PostMapping
    public Result<Character> createCharacter(@RequestBody Character character) {
        Character createdCharacter = characterService.createCharacter(character);
        return Result.success("创建成功", createdCharacter);
    }
    
    /**
     * 更新角色
     */
    @PutMapping("/{id}")
    public Result<Character> updateCharacter(@PathVariable String id, @RequestBody Character character) {
        Character updatedCharacter = characterService.updateCharacter(id, character);
        return Result.success("更新成功", updatedCharacter);
    }
    
    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteCharacter(@PathVariable String id) {
        characterService.deleteCharacter(id);
        return Result.success("删除成功");
    }
    
}



