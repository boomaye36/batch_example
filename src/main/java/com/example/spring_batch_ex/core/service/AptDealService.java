package com.example.spring_batch_ex.core.service;

import com.example.spring_batch_ex.core.entity.Apt;
import com.example.spring_batch_ex.core.entity.AptDeal;
import com.example.spring_batch_ex.core.repository.AptDealRepository;
import com.example.spring_batch_ex.core.repository.AptRepository;
import com.example.spring_batch_ex.dto.AptDealDto;
import com.example.spring_batch_ex.dto.AptDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * AptDealDto에 있는 값을 Apt, AptDeal 엔티티로 지정한다.
 */
@AllArgsConstructor
@Service
public class AptDealService {
    private final AptRepository aptRepository;
    private final AptDealRepository aptDealRepository;

    @Transactional
    public void upsert(AptDealDto aptDealDto) {
        Apt apt = getAptOrNew(aptDealDto);
        saveAptDeal(aptDealDto, apt);
    }

    private void saveAptDeal(AptDealDto aptDealDto, Apt apt) {
        AptDeal aptDeal = aptDealRepository.findAptDealByAptAndExclusiveAreaAndDealDateAndDealAmountAndFloor(
                apt, aptDealDto.getExclusiveArea(), aptDealDto.getDealDate(), aptDealDto.getDealAmount(), aptDealDto.getFloor()
        ).orElseGet(() -> AptDeal.of(aptDealDto, apt));
        aptDeal.setDealCanceledDate(aptDealDto.getDealCanceledDate());
        aptDeal.setDealCanceled(aptDealDto.isDealCanceled());
        aptDealRepository.save(aptDeal);
    }

//    private void saveAptDeal(AptDealDto aptDealDto, Apt apt) {
//        // 데이터베이스에서 해당 정보를 조회합니다.
//        Optional<AptDeal> existingAptDeal = aptDealRepository.findAptDealByAptAndExclusiveAreaAndDealDateAndDealAmountAndFloor(
//                apt, aptDealDto.getExclusiveArea(), aptDealDto.getDealDate(), aptDealDto.getDealAmount(), aptDealDto.getFloor()
//        );
//
//        if (existingAptDeal.isPresent()) {
//            // 데이터가 조회되었을 경우, 값을 업데이트합니다.
//            AptDeal aptDeal = existingAptDeal.get();
//            aptDeal.setDealCanceledDate(aptDealDto.getDealCanceledDate());
//            aptDeal.setDealCanceled(aptDealDto.isDealCanceled());
//            aptDealRepository.save(aptDeal);
//        } else {
//            // 데이터가 조회되지 않았을 경우, 새로운 데이터를 생성하고 저장합니다.
//            AptDeal aptDeal = AptDeal.of(aptDealDto, apt);
//            aptDealRepository.save(aptDeal);
//        }
//    }

    private Apt getAptOrNew(AptDealDto aptDealDto) {
        Apt apt = aptRepository.findAptByAptNameAndJibun(aptDealDto.getAptName(), aptDealDto.getJibun())
                .orElseGet(() -> Apt.from(aptDealDto)); // 값이 존재하지 않으면 dto로부터 값을 set

        aptRepository.save(apt);
        return apt;


    }

    public List<AptDto> findByGuLawdCdAndDealDate(String guLawdCd, LocalDate dealDate) {
        return aptDealRepository.findByDealCanceledIsFalseAndDealDateEquals(dealDate)
                .stream()
                .filter(aptDeal -> aptDeal.getApt().getGuLawdCd().equals(guLawdCd))
                .map(aptDeal -> new AptDto(aptDeal.getApt().getAptName(), aptDeal.getDealAmount()))
                .collect(Collectors.toList());
    }

}
