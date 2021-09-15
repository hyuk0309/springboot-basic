package org.prgrms.kdt.jdbcRepository;

import org.prgrms.kdt.domain.CustomerEntity;
import org.prgrms.kdt.domain.CustomerVoucherEntity;
import org.prgrms.kdt.repository.CustomerVoucherRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static org.prgrms.kdt.utill.EntityUtil.*;

@Repository
public class CustomerVoucherJdbcRepository implements CustomerVoucherRepository {

    private final Logger logger = LoggerFactory.getLogger(VoucherJdbcRepository.class);

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final String SELECT_BY_CUSTOMER_ID_SQL = "select voucher_id from customer_voucher where customer_id = UUID_TO_BIN(:customerId)";
    private final String SELECT_ALL_SQL = "select * from customer_voucher";
    private final String SELECT_BY_ID = "select * from customer_voucher where customer_voucher_id = UUID_TO_BIN(:customerVoucherId)";
    private final String INSERT_SQL = "insert into customer_voucher(customer_voucher_id,customer_id, voucher_id, created_at) values(UUID_TO_BIN(:customerVoucherId),UUID_TO_BIN(:customerId),UUID_TO_BIN(:voucherId), :createdAt)";
    private final String DELETE_ALL_SQL = "delete from customer_voucher";
    private final String DELETE_BY_ID_SQL = "delete from customer_voucher where customer_id = UUID_TO_BIN(:customerId) AND voucher_id = UUID_TO_BIN(:voucherId)";
    private final String SELECT_BY_VOUCHER_ID_SQL = "select c.customer_id, c.email, c.name, c.last_login_at, c.created_at from" +
            " customer_voucher as cv right join customers as c on c.customer_id = cv.customer_id where cv.voucher_id = UUID_TO_BIN(:voucherId)";

    public CustomerVoucherJdbcRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public CustomerVoucherEntity insert(CustomerVoucherEntity customerVoucherEntity) {
        var update = jdbcTemplate.update(INSERT_SQL, toParamMap(customerVoucherEntity));
        if (update != 1) {
            throw new RuntimeException("Nothing was Inserted");
        }
        return customerVoucherEntity;
    }

    @Override
    public void deleteAll() {
        jdbcTemplate.update(DELETE_ALL_SQL, Collections.emptyMap());
    }

    @Override
    public void deleteById(UUID customerId, UUID voucherId) {
        Map<String, byte[]> idList = new HashMap<>() {{
            put("customerId", customerId.toString().getBytes());
            put("voucherId", voucherId.toString().getBytes());
        }};
        jdbcTemplate.update(DELETE_BY_ID_SQL, idList);
    }

    @Override
    public List<CustomerVoucherEntity> findAll() {
        return jdbcTemplate.query(SELECT_ALL_SQL, customerVoucherEntityRowMapper);
    }

    @Override
    public Optional<CustomerVoucherEntity> findById(UUID customerVoucherId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(SELECT_BY_ID, Collections.singletonMap("customerVoucherId",
                            customerVoucherId.toString().getBytes()),
                    customerVoucherEntityRowMapper));
        } catch (EmptyResultDataAccessException e) {
            logger.error("Got Empty result", e);
            return Optional.empty();
        }
    }

    @Override
    public List<UUID> findByCustomerId(UUID customerId) {
            return jdbcTemplate.query(SELECT_BY_CUSTOMER_ID_SQL,
                    Collections.singletonMap("customerId", customerId.toString().getBytes()),
                    new RowMapper<UUID>() {
                        @Override
                        public UUID mapRow(ResultSet resultSet, int i) throws SQLException {
                            return toUUID(resultSet.getBytes("voucher_id"));
                        }
                    });
    }

    @Override
    public Optional<CustomerEntity> findByVoucherId(UUID voucherId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(SELECT_BY_VOUCHER_ID_SQL,
                    Collections.singletonMap("voucherId", voucherId.toString().getBytes()),
                    customerEntityRowMapper));
        } catch (EmptyResultDataAccessException e) {
            logger.error("Got Empty result", e);
            return Optional.empty();
        }
    }
}