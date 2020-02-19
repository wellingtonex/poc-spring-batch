package com.example.pocspringbatch.jobs;

import com.example.pocspringbatch.mappers.ContaRowMapper;
import com.example.pocspringbatch.models.Conta;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.math.BigDecimal;

@Configuration
public class JobSimples {

    @Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

    @Bean
    public JdbcCursorItemReader<Conta> constaCursorItemReader(DataSource dataSource) {
        return new JdbcCursorItemReaderBuilder<Conta>()
                .name("constaCursorItemReader")
                .dataSource(dataSource)
                .sql("select * from conta")
                .rowMapper(new ContaRowMapper())
                .build();
    }

    @Bean
	public ItemWriter<Conta> itemWriter() {
		return (items) -> items.forEach(conta -> {
			System.out.println("Thread name: " + Thread.currentThread().getName() + " Thread id "  + Thread.currentThread().getId() + " " + conta);
		});
	}

	@Bean
	public ItemProcessor<Conta, Conta> itemProcessor() {
		return (conta) ->  {
			conta.setSaldo(conta.getSaldo().negate());
			return conta;
		};
	}

    @Bean
	public Step simplesStepContas() {
		return this.stepBuilderFactory.get("simplesStepContas")
				.<Conta, Conta>chunk(10)
				.reader(constaCursorItemReader(null))
				.processor(itemProcessor())
				.writer(itemWriter())
				.build();
	}

    @Bean
	public Job job() {
		return this.jobBuilderFactory.get("jobSimplesContas")
				.start(simplesStepContas())
				.build();
	}

	private class Teste implements  ItemProcessor<Conta, Conta> {

		@Override
		public Conta process(Conta conta) throws Exception {
			return null;
		}
	}
}
