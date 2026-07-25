package com.cs.url_shortner_api.repository;

import com.cs.url_shortner_api.model.ClickEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClickEventRepository extends JpaRepository<ClickEvent, Long> {
}
