package com.gpb.web.repository;

import com.gpb.web.bean.user.WebMessengerConnector;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WebMessengerConnectorRepository extends CrudRepository<WebMessengerConnector, String> {


}
