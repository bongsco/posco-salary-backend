package com.bongsco.poscosalarybackend.adjust.service;

import org.springframework.stereotype.Service;

import com.bongsco.poscosalarybackend.adjust.repository.AdjustRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AdjustService {
    private final AdjustRepository mainRepository;
}
