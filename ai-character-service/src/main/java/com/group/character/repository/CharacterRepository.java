package com.group.character.repository;

import com.group.common.entity.Character;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 角色数据访问层
 */
@Repository
public interface CharacterRepository extends MongoRepository<Character, String> {
    
    List<Character> findByCategoryAndStatus(String category, Integer status);
    
    List<Character> findByStatusOrderByPopularityDesc(Integer status);
    
}
