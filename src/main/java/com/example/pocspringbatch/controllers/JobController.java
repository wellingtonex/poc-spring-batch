package com.example.pocspringbatch.controllers;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;

import org.springframework.context.ApplicationContext;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.Date;

@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
public class JobController {

    private final DataSource dataSource;
    private final JobLauncher jobLauncher;
    private final ApplicationContext context;

    @GetMapping("/criar-contas/{quantidade}")
    public String criarContas(@PathVariable Integer quantidade) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        for (int i = 0; i < quantidade; i++) {
            jdbcTemplate.update(
                    "insert into conta(saldo, ultimaAtualizacao) values (?, ?);",
                    BigDecimal.valueOf(10).multiply(BigDecimal.valueOf(i)), new Date());
        }

        return "Contas inseridas com sucesso";
    }

    @SneakyThrows
    @PostMapping("/executar")
    public BatchStatus executar(@RequestBody @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) JobPameterDTO jobPameterDTO) {
        Job job = this.context.getBean("jobSimplesContas", Job.class);
        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
        jobParametersBuilder.addDate("data", jobPameterDTO.getDate());
        return this.jobLauncher.run(job, jobParametersBuilder.toJobParameters()).getStatus();
    }
}
