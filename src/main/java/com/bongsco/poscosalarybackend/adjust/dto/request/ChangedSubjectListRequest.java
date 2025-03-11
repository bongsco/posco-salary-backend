package com.bongsco.poscosalarybackend.adjust.dto.request;

import java.util.List;

import lombok.Getter;
@Getter
public class ChangedSubjectListRequest {
    private final List<ChangedSubjectRequest> changedSubject;

    public ChangedSubjectListRequest(List<ChangedSubjectRequest> changedSubject) {
        this.changedSubject = changedSubject;
    }
}
