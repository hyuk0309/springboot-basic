package org.prgrms.springbootbasic.repository.voucher;

import static com.wix.mysql.EmbeddedMysql.anEmbeddedMysql;
import static com.wix.mysql.ScriptResolver.classPathScript;
import static com.wix.mysql.config.MysqldConfig.aMysqldConfig;
import static com.wix.mysql.distribution.Version.v8_0_11;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.prgrms.springbootbasic.repository.DBErrorMsg.NOTHING_WAS_DELETED_EXP_MSG;

import com.wix.mysql.EmbeddedMysql;
import com.wix.mysql.config.Charset;
import com.zaxxer.hikari.HikariDataSource;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.sql.DataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.prgrms.springbootbasic.controller.VoucherType;
import org.prgrms.springbootbasic.entity.customer.Customer;
import org.prgrms.springbootbasic.entity.customer.Email;
import org.prgrms.springbootbasic.entity.customer.Name;
import org.prgrms.springbootbasic.entity.voucher.FixedAmountVoucher;
import org.prgrms.springbootbasic.entity.voucher.PercentDiscountVoucher;
import org.prgrms.springbootbasic.entity.voucher.Voucher;
import org.prgrms.springbootbasic.repository.customer.JdbcCustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig
@TestInstance(Lifecycle.PER_CLASS)
class JdbcVoucherRepositoryTest {

    @Autowired
    JdbcVoucherRepository jdbcVoucherRepository;

    @Autowired
    JdbcCustomerRepository jdbcCustomerRepository;

    EmbeddedMysql embeddedMysql;

    @BeforeAll
    void setup() {
        var mysqldConfig = aMysqldConfig(v8_0_11)
            .withCharset(Charset.UTF8)
            .withPort(2215)
            .withUser("test", "test1234!")
            .withTimeZone("Asia/Seoul")
            .build();
        embeddedMysql = anEmbeddedMysql(mysqldConfig)
            .addSchema("test_springboot_basic", classPathScript("schema.sql"))
            .start();
    }

    @AfterAll
    void clean() {
        embeddedMysql.stop();
    }

    @AfterEach
    void init() {
        jdbcVoucherRepository.removeAll();
        jdbcCustomerRepository.removeAll();
    }

    @DisplayName("모든 바우처 조회 기능")
    @Test
    void testFindAll() {
        //given
        jdbcVoucherRepository.insert(new FixedAmountVoucher(UUID.randomUUID(), 100));
        jdbcVoucherRepository.insert(new PercentDiscountVoucher(UUID.randomUUID(), 20));

        //when
        var vouchers = jdbcVoucherRepository.findAll();

        //then
        assertThat(vouchers.size()).isEqualTo(2);
    }

    @DisplayName("모든 바우처 조회")
    @Test
    void testFindAllEmpty() {
        //given
        //when
        var vouchers = jdbcVoucherRepository.findAll();

        //then
        assertThat(vouchers).isEmpty();
    }

    @DisplayName("FixedAmountVoucher 저장 테스트")
    @Test
    void testInsert() {
        //given
        Voucher voucher = new FixedAmountVoucher(UUID.randomUUID(), 1000);

        //when
        jdbcVoucherRepository.insert(voucher);

        //then
        var foundVoucher = jdbcVoucherRepository.findById(voucher.getVoucherId());
        assertThat(foundVoucher.isPresent()).isTrue();
        assertThat(foundVoucher.get().getVoucherId()).isEqualTo(voucher.getVoucherId());
        assertThat(foundVoucher.get().getClass()).isEqualTo(voucher.getClass());
    }

    @DisplayName("PercentAmountVoucher 저장 테스트")
    @Test
    void testInsertPercentAmountVoucher() {
        //given
        Voucher voucher = new PercentDiscountVoucher(UUID.randomUUID(), 10);

        //when
        jdbcVoucherRepository.insert(voucher);

        //then
        var foundVoucher = jdbcVoucherRepository.findById(voucher.getVoucherId());
        assertThat(foundVoucher.isPresent()).isTrue();
        assertThat(foundVoucher.get().getVoucherId()).isEqualTo(voucher.getVoucherId());
        assertThat(foundVoucher.get().getClass()).isEqualTo(voucher.getClass());
    }

    @DisplayName("customer_id 수정 테스트")
    @Test
    void testUpdateCustomerId() {
        //given
        var voucher = new FixedAmountVoucher(UUID.randomUUID(), 1000);
        jdbcVoucherRepository.insert(voucher);

        var customer = new Customer(UUID.randomUUID(), new Name("test"),
            new Email("test@gmail.com"));
        jdbcCustomerRepository.insert(customer);

        voucher.assignCustomer(customer);

        //when
        jdbcVoucherRepository.updateCustomerId(voucher);

        //then
        var updatedVoucher = jdbcVoucherRepository.findById(voucher.getVoucherId());
        assertThat(updatedVoucher.get().getCustomerId())
            .isEqualTo(voucher.getCustomerId());
    }

    @DisplayName("특정 customer의 바우처 조회 기능 - 바우처가 존재한는 경우")
    @Test
    void testFindByCustomer() {
        //given
        var voucher = new FixedAmountVoucher(UUID.randomUUID(), 1000);
        jdbcVoucherRepository.insert(voucher);

        var customer = new Customer(UUID.randomUUID(), new Name("test"),
            new Email("test@gmail.com"));
        jdbcCustomerRepository.insert(customer);

        voucher.assignCustomer(customer);
        jdbcVoucherRepository.updateCustomerId(voucher);

        //when
        var vouchers = jdbcVoucherRepository.findByCustomer(customer);

        //then
        assertThat(vouchers.size()).isEqualTo(1);
        assertThat(vouchers.get(0)).isEqualTo(voucher);
    }

    @DisplayName("특정 customer의 바우처 조회 기능 - 바우처가 존재하지 않는 경우")
    @Test
    void testFindByCustomerButEmpty() {
        //given
        var voucher = new FixedAmountVoucher(UUID.randomUUID(), 1000);
        jdbcVoucherRepository.insert(voucher);

        var customer = new Customer(UUID.randomUUID(), new Name("test"),
            new Email("test@gmail.com"));
        jdbcCustomerRepository.insert(customer);

        //when
        var vouchers = jdbcVoucherRepository.findByCustomer(customer);

        //then
        assertThat(vouchers).isEmpty();
    }

    @DisplayName("특정 바우처 삭제")
    @Test
    void testDeleteVoucher() {
        //given
        var voucher = new PercentDiscountVoucher(UUID.randomUUID(), 20);
        jdbcVoucherRepository.insert(voucher);

        //when
        jdbcVoucherRepository.deleteVoucher(voucher);

        //then
        assertThat(jdbcVoucherRepository.findById(voucher.getVoucherId())).isEmpty();
    }

    @DisplayName("특정 바우처 삭제 - 존재하지 않는 바우처 삭제")
    @Test
    void testDeleteInvalidVoucher() {
        //given
        //when
        //then
        assertThatThrownBy(() -> jdbcVoucherRepository.deleteVoucher(
            new FixedAmountVoucher(UUID.randomUUID(), 20)))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining(NOTHING_WAS_DELETED_EXP_MSG.getMessage());
    }

    @DisplayName("바우처 fixed 타입으로 바우처 조회")
    @Test
    void testFindByFixType() {
        //given
        var voucher = new FixedAmountVoucher(UUID.randomUUID(), 1000);
        var voucher2 = new FixedAmountVoucher(UUID.randomUUID(), 2000);

        jdbcVoucherRepository.insert(voucher);
        jdbcVoucherRepository.insert(voucher2);

        //when
        var vouchers = jdbcVoucherRepository.findByType(VoucherType.FIXED);

        //then
        assertThat(vouchers)
            .containsExactlyInAnyOrder(voucher, voucher2);
    }

    @DisplayName("바우처 percnet 타입으로 바우처 조회")
    @Test
    void testFindByPercentPercent() {
        //given
        var voucher = new PercentDiscountVoucher(UUID.randomUUID(), 20);
        var voucher2 = new PercentDiscountVoucher(UUID.randomUUID(), 20);

        jdbcVoucherRepository.insert(voucher);
        jdbcVoucherRepository.insert(voucher2);

        //when
        var vouchers = jdbcVoucherRepository.findByType(VoucherType.PERCENT);

        //then
        assertThat(vouchers)
            .containsExactlyInAnyOrder(voucher, voucher2);
    }

    @Test
    @DisplayName("생성일(날짜) 기준 바우처 조회")
    void testFindByCreatedAt() {
        //given
        var voucher1 = new FixedAmountVoucher(UUID.randomUUID(), 1000);
        var voucher2 = new PercentDiscountVoucher(UUID.randomUUID(), 20);

        jdbcVoucherRepository.insert(voucher1);
        jdbcVoucherRepository.insert(voucher2);

        //when
        var vouchers = jdbcVoucherRepository.findByCreatedAt(
            LocalDateTime.now(), LocalDateTime.now());

        //then
        assertThat(vouchers)
            .containsExactlyInAnyOrder(voucher1, voucher2);
    }

    @Configuration
    @ComponentScan
    static class Config {

        @Bean
        public DataSource dataSource() {
            return DataSourceBuilder.create()
                .url("jdbc:mysql://localhost:2215/test_springboot_basic")
                .username("test")
                .password("test1234!")
                .type(HikariDataSource.class)
                .build();
        }

        @Bean
        public JdbcTemplate jdbcTemplate(DataSource dataSource) {
            return new JdbcTemplate(dataSource);
        }

        @Bean
        public JdbcVoucherRepository jdbcVoucherRepository(JdbcTemplate jdbcTemplate) {
            return new JdbcVoucherRepository(jdbcTemplate);
        }

        @Bean
        public JdbcCustomerRepository jdbcCustomerRepository(JdbcTemplate jdbcTemplate,
            DataSource dataSource) {
            return new JdbcCustomerRepository(jdbcTemplate);
        }
    }
}
