package com.example.pocspringbatch.jobs;

import com.example.pocspringbatch.mappers.ContaRowMapper;
import com.example.pocspringbatch.models.Conta;
import com.example.pocspringbatch.models.Movimento;
import com.example.pocspringbatch.models.Operacao;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Date;
import java.util.UUID;

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
				.pageSize(500)
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
	public ItemWriter<Operacao> itemWriter(JdbcTemplate jdbcTemplate) {
		return (operacoes) -> operacoes.forEach(operacao -> {
			KeyHolder keyHolder = new GeneratedKeyHolder();
			jdbcTemplate.update(connection -> {
				PreparedStatement ps = connection
						.prepareStatement("insert into operacao (tx) values(?) ", Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, operacao.getTx());
				return ps;
			}, keyHolder);

			BigInteger id = (BigInteger) keyHolder.getKey();
			operacao.setId(id.intValue());

			operacao.getMovimentos().stream().forEach(movimento -> {
				jdbcTemplate.update("insert into movimento (valor, contaId, operacaoID, data) values(?, ?, ?, ?) ",
						movimento.getValor(), movimento.getConta().getId(), movimento.getOperacao().getId(), new java.sql.Date(movimento.getData().getTime()));

				jdbcTemplate.update("update conta set saldo = ? where conta.id = ? ",
						movimento.getConta().getSaldo().add(movimento.getValor()), movimento.getConta().getId());

			});
		});
	}

	@Bean
	public ItemProcessor<Conta, Operacao> itemProcessor() {
		return (conta) ->  {
			conta.setSaldo(conta.getSaldo().subtract(BigDecimal.ONE));

			Operacao operacao = new Operacao(UUID.randomUUID().toString());

			var movimento = Movimento.builder()
					.conta(conta)
					.data(new Date())
					.operacao(operacao)
					.valor(BigDecimal.ONE)
					.build();

			operacao.addMovimentos(movimento);
			return operacao;
		};
	}

    @Bean
	public Step simplesStepContas() {
		return this.stepBuilderFactory.get("simplesStepContas")
				.<Conta, Operacao>chunk(100)
				.reader(constaCursorItemReaderPaginatede(null, null))
				.processor(itemProcessor())
				.writer(itemWriter(null))
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
