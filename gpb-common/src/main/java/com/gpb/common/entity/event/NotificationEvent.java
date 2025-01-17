package com.gpb.common.entity.event;

import com.gpb.common.entity.game.GameInStoreDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationEvent {

    private long basicUserId;

    private List<GameInStoreDto> gameInShopList;
}
