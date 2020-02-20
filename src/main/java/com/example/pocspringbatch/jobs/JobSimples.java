package com.example.pocspringbatch.jobs;

import com.example.pocspringbatch.mappers.ContaRowMapper;
import com.example.pocspringbatch.models.Conta;
import lombok.SneakyThrows;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.sql.DataSource;

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
	@SneakyThrows
	public JdbcPagingItemReader<Conta> constaCursorItemReaderPaginatede(DataSource dataSource, PagingQueryProvider queryProvider) {
		dataSource.getConnection().setSchema("xpto");
		return new JdbcPagingItemReaderBuilder<Conta>()
				.name("constaCursorItemReaderPaginatede")
				.dataSource(dataSource)
				.queryProvider(queryProvider)
				.pageSize(100)
				.rowMapper(new ContaRowMapper())
				.build();
	}

	@Bean
	public TaskExecutor asyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(5);
		executor.setMaxPoolSize(15);
		executor.setQueueCapacity(50);
		executor.setThreadNamePrefix("POC");
		executor.initialize();
		return executor;
	}


	@Bean
	public SqlPagingQueryProviderFactoryBean pagingQueryProvider(DataSource dataSource) {
		SqlPagingQueryProviderFactoryBean factoryBean = new SqlPagingQueryProviderFactoryBean();

		factoryBean.setDataSource(dataSource);
		factoryBean.setSelectClause("select *");
		factoryBean.setFromClause("from conta");
//		factoryBean.setWhereClause("where city = :city");
		factoryBean.setSortKey("id");

		return factoryBean;
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
				.<Conta, Conta>chunk(100)
				.reader(constaCursorItemReaderPaginatede(null, null))
				.processor(itemProcessor())
				.writer(itemWriter())
				.taskExecutor(asyncExecutor())
				.build();
	}

    @Bean("jobSimplesContas")
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
