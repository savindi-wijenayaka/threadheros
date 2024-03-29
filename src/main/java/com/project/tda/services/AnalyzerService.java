package com.project.tda.services;

import com.google.gson.Gson;
import com.project.tda.models.ThreadDumps;
import com.project.tda.repositories.ThreadDumpsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class AnalyzerService {
    BasicAnalyzerService basicAnalyzerService;
    FurtherAnalyzerService furtherAnalyzerService;
    private ThreadDumpsRepo threadDumpsRepo;

    public AnalyzerService(ThreadDumpsRepo threadDumpsRepo,BasicAnalyzerService basicAnalyzerService,FurtherAnalyzerService furtherAnalyzerService){
        this.basicAnalyzerService = basicAnalyzerService;
        this.furtherAnalyzerService =furtherAnalyzerService;
        this.threadDumpsRepo=threadDumpsRepo;
    }

    public String analyze(String dump, String username, String filename){
        String result;
        Map<String, Object> message = new HashMap<>();
        ArrayList<SingleThreadAnalyzerService> basicAnalysis = basicAnalyzerService.generateAnalysis(dump);
        Map<String, SynchronizerAnalysisService> syncMap = basicAnalyzerService.getSync();
        String date = basicAnalyzerService.getDate();

        HashMap<String, SingleThreadAnalyzerService> threadAnalysisMap = new HashMap<>();
        for (SingleThreadAnalyzerService singleThread : basicAnalysis) {
            threadAnalysisMap.put(singleThread.id, singleThread);
        }

        result = new Gson().toJson(basicAnalysis);

        message.put("thread_array", basicAnalysis);
        message.put("sync_map", syncMap);
        message.put("thread_map", threadAnalysisMap);
        message.put("state_vise", furtherAnalyzerService.stateviseSummary(result));
        message.put("deamons", furtherAnalyzerService.deamonSummary(result));
        message.put("stack_length", furtherAnalyzerService.stackLengthSummary(result));
        message.put("identicle_stack_report", furtherAnalyzerService.identicalStackTraceSummary(result));
        message.put("identicle_stack_map", furtherAnalyzerService.getStackTraceMap());

        result =  new Gson().toJson(message);

        if(!(username.equals("sample"))){
            System.out.println(date);
            ThreadDumps threadDumps = threadDumpsRepo.save(new ThreadDumps(filename, result, username, date));
            System.out.println(threadDumps.getThreadId());
        }

        basicAnalysis.clear();
        return result;
    }
}
