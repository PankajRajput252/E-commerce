package com.gunwala.repo;

import com.gunwala.model.entitities.enduser.UserMap;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserMapRepository  extends JpaRepository<UserMap, Integer> {
    UserMap findByUserNodeIdAndActiveStateCodeFkId(String parentNodeId, String active);
}
