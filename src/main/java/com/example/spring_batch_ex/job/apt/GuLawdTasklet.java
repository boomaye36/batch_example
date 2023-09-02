package com.example.spring_batch_ex.job.apt;

import com.example.spring_batch_ex.core.repository.LawdRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;

import java.util.List;
@RequiredArgsConstructor
public class GuLawdTasklet implements Tasklet {
    private final LawdRepository lawdRepository;
    private List<String> guLawdCdList;
    private int itemCount;
    private static final String KEY_ITEM_COUNT = "itemCount";
    private static final String KEY_GU_LAWD_CD_List = "guLawdCdList";
    private static final String KEY_GU_LAWD_CD = "guLawdCd";
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        ExecutionContext executionContext = getExecutionContext(chunkContext);
        // 데이터가 있으면 다음 스텝을 실행하도록 하고, 데이터가 없으면 종료하도록 한다.
        // 데이터가 있으면 -> CONTINUABLE

        // 1. guLawdCdList
        // 2. guLawdCd
        // 3. itemCount
        inItList(executionContext);
        initItemCount(executionContext);
        if (itemCount == 0){
            contribution.setExitStatus(ExitStatus.COMPLETED);
            return RepeatStatus.FINISHED;
        }
        itemCount--;
        String guLawdCd = guLawdCdList.get(itemCount);

        executionContext.putString(KEY_GU_LAWD_CD, guLawdCd);
        executionContext.putInt(KEY_ITEM_COUNT, itemCount);

        contribution.setExitStatus(new ExitStatus("CONTINUABLE"));
        return RepeatStatus.FINISHED;
    }

    private void inItList(ExecutionContext executionContext) {
        if (!executionContext.containsKey(KEY_GU_LAWD_CD_List)) {
            guLawdCdList = lawdRepository.findDistinctGuLawdCd();
            executionContext.put(KEY_GU_LAWD_CD_List, guLawdCdList);
            executionContext.putInt(KEY_ITEM_COUNT, guLawdCdList.size());
        } else {
            guLawdCdList = (List<String>) executionContext.get(KEY_GU_LAWD_CD_List);
        }
    }

    private void initItemCount(ExecutionContext executionContext){
        if (executionContext.containsKey(KEY_ITEM_COUNT)) {
           itemCount = executionContext.getInt(KEY_ITEM_COUNT);
        } else {
            itemCount = guLawdCdList.size();
        }
    }
    private static ExecutionContext getExecutionContext(ChunkContext chunkContext) {
        StepExecution stepExecution = chunkContext.getStepContext().getStepExecution();
        ExecutionContext executionContext = stepExecution.getJobExecution().getExecutionContext();
        return executionContext;
    }

}
