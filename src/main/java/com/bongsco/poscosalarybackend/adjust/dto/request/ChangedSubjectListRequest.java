package com.bongsco.poscosalarybackend.adjust.dto.request;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ChangedSubjectListRequest {
    private List<ChangedSubjectRequest> changedSubject;
}
