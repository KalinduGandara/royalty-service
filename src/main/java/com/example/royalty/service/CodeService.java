package com.example.royalty.service;

import com.example.royalty.modal.Code;
import com.example.royalty.repository.CodeRepository;
import org.springframework.stereotype.Service;

@Service

public class CodeService {
    private final CodeRepository codeRepository;


    public CodeService(CodeRepository codeRepository) {
        this.codeRepository = codeRepository;
    }

    public Code getByCode(String code) {
        return codeRepository.findByCode(code);
    }

    public void markAsUsed(Code code) {
        code.setUsed(true);
        codeRepository.save(code);
    }
}
