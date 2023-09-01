package com.example.spring_batch_ex.core.service;

import com.example.spring_batch_ex.core.entity.Lawd;
import com.example.spring_batch_ex.core.repository.LawdRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class LawdService {
    private final LawdRepository lawdRepository;

    @Transactional
    public void upsert(Lawd lawd){
        // 데이터가 존재하면 수정 없으면 입력
        Lawd saved = lawdRepository.findByLawdCd(lawd.getLawdCd())
                .orElseGet(Lawd::new);
        saved.setLawdCd(lawd.getLawdCd());
        saved.setLawdDong(lawd.getLawdDong());
        saved.setExist(lawd.isExist());
        lawdRepository.save(saved);
    }
}
