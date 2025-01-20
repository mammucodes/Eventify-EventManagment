package com.example.eventifyeventmanagment.loaders;

import com.example.eventifyeventmanagment.entity.Status;
import com.example.eventifyeventmanagment.repository.StatusRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class EventStatusStaticLoader {
    private final StatusRepository statusRepository;
    private Map<Integer,String> staticSatatusDataMap ;
    private Map<String,Integer> statusNameDataMap;
@Autowired
    public EventStatusStaticLoader(StatusRepository statusRepository){
        this.statusRepository =statusRepository;

    }

@PostConstruct // this annoatation tells to run this method wheenver spring boot application runs. before calling it and  load the data from database
    public void  loadStatusData() {
    List<Status> statusData = statusRepository.findAll();
    staticSatatusDataMap = new HashMap<>();
    statusNameDataMap = new HashMap<>();
    for (Status status : statusData) {
        staticSatatusDataMap.put(status.getId(), status.getStatus());
        statusNameDataMap.put(status.getStatus(),status.getId());
    }


}
public Integer getStatusIdByUsingStatusName( String statusName){
    Integer statusId = statusNameDataMap.get(statusName);
    return statusId;
}

    public String getStatusNameByUsingStatusId( Integer statusId){
        String statusName = staticSatatusDataMap.get(statusId);
        return statusName;
    }

}

