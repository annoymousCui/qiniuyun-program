package com.group.character.service;

import com.group.common.entity.Character;
import com.group.character.repository.CharacterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 角色服务
 */
@Service
public class CharacterService {
    
    @Autowired
    private CharacterRepository characterRepository;
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    /**
     * 搜索角色
     */
    public List<Character> searchCharacters(String keyword, String category, int page, int size) {
        Query query = new Query();
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            query.addCriteria(Criteria.where("name").regex(keyword, "i")
                    .orOperator(Criteria.where("description").regex(keyword, "i")));
        }
        
        if (category != null && !category.trim().isEmpty()) {
            query.addCriteria(Criteria.where("category").is(category));
        }
        
        query.addCriteria(Criteria.where("status").is(1));
        query.with(PageRequest.of(page, size));
        query.with(org.springframework.data.domain.Sort.by("popularity").descending());
        
        return mongoTemplate.find(query, Character.class);
    }
    
    /**
     * 获取热门角色
     */
    public List<Character> getPopularCharacters(int limit) {
        Query query = new Query();
        query.addCriteria(Criteria.where("status").is(1));
        query.with(PageRequest.of(0, limit));
        query.with(org.springframework.data.domain.Sort.by("popularity").descending());
        
        return mongoTemplate.find(query, Character.class);
    }
    
    /**
     * 根据ID获取角色
     */
    public Character getCharacterById(String id) {
        Optional<Character> character = characterRepository.findById(id);
        if (!character.isPresent()) {
            throw new RuntimeException("角色不存在");
        }
        return character.get();
    }
    
    /**
     * 创建角色
     */
    public Character createCharacter(Character character) {
        character.setStatus(1);
        character.setPopularity(0);
        character.setCreateTime(LocalDateTime.now());
        character.setUpdateTime(LocalDateTime.now());
        return characterRepository.save(character);
    }
    
    /**
     * 更新角色
     */
    public Character updateCharacter(String id, Character updateCharacter) {
        Character character = getCharacterById(id);
        
        if (updateCharacter.getName() != null) {
            character.setName(updateCharacter.getName());
        }
        if (updateCharacter.getDescription() != null) {
            character.setDescription(updateCharacter.getDescription());
        }
        if (updateCharacter.getPersonality() != null) {
            character.setPersonality(updateCharacter.getPersonality());
        }
        if (updateCharacter.getBackground() != null) {
            character.setBackground(updateCharacter.getBackground());
        }
        if (updateCharacter.getAvatar() != null) {
            character.setAvatar(updateCharacter.getAvatar());
        }
        if (updateCharacter.getTags() != null) {
            character.setTags(updateCharacter.getTags());
        }
        if (updateCharacter.getCategory() != null) {
            character.setCategory(updateCharacter.getCategory());
        }
        
        character.setUpdateTime(LocalDateTime.now());
        return characterRepository.save(character);
    }
    
    /**
     * 删除角色
     */
    public void deleteCharacter(String id) {
        Character character = getCharacterById(id);
        character.setStatus(0);
        character.setUpdateTime(LocalDateTime.now());
        characterRepository.save(character);
    }
    
}
