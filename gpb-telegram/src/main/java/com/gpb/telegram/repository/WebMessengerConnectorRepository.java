package com.gpb.telegram.repository;

import com.gpb.telegram.bean.WebMessengerConnector;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WebMessengerConnectorRepository  extends CrudRepository<WebMessengerConnector, String> {

}
