package com.gpb.web.service.impl;

import com.gpb.web.bean.game.Game;
import com.gpb.web.exception.NotFoundException;
import com.gpb.web.service.impl.GameStoresServiceImpl;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GameStoresServiceImplTest {

    @Mock
    private KafkaTemplate<String, String> kafkaGameSearchTemplate;

    @Mock
    private KafkaTemplate<String, Long> kafkaFollowTemplate;

    @InjectMocks
    private GameStoresServiceImpl gameStoresService;

    private static final String CORRELATION_ID = UUID.randomUUID().toString();

    @BeforeEach
    void setUp() {
        reset(kafkaGameSearchTemplate);
    }

    @Test
    public void testFindGameByName_Success_Should() throws InterruptedException {
        String name = "Test Game";
        List<Game> expectedGames = Collections.singletonList(new Game());

        CountDownLatch latch = new CountDownLatch(1);
        ArgumentCaptor<ProducerRecord<String, String>> captor = ArgumentCaptor.forClass(ProducerRecord.class);

        doAnswer(invocation -> {
            ProducerRecord<String, String> record = invocation.getArgument(0);
            assertEquals("gpb_game_name_search_request", record.topic());
            assertNotNull(record.key());
            assertEquals(name, record.value());
            latch.countDown();
            return null;
        }).when(kafkaGameSearchTemplate).send(captor.capture());

        assertThrows(NotFoundException.class, () -> gameStoresService.findGameByName(name));

        assertTrue(latch.await(10, TimeUnit.SECONDS));
    }

    @Test
    public void testFindGameByName_NotFound() throws InterruptedException {
        String name = "Test Game";

        CountDownLatch latch = new CountDownLatch(1);
        ArgumentCaptor<ProducerRecord<String, String>> captor = ArgumentCaptor.forClass(ProducerRecord.class);

        doAnswer(invocation -> {
            ProducerRecord<String, String> record = invocation.getArgument(0);
            assertEquals("gpb_game_name_search_request", record.topic());
            assertNotNull(record.key());
            assertEquals(name, record.value());
            latch.countDown();
            return null;
        }).when(kafkaGameSearchTemplate).send(captor.capture());

        assertThrows(NotFoundException.class, () -> gameStoresService.findGameByName(name));

        assertTrue(latch.await(10, TimeUnit.SECONDS));
    }


}