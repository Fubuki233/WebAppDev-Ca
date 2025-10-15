package sg.com.aori.interfaces;

import java.util.List;

import sg.com.aori.model.ViewHistory;

/**
 * ViewHistory interface for managing product view history.
 * 
 * @author Yunhe
 * @date 2025-10-12
 * @version 1.0
 */

public interface IViewHistory {
    String addView(String id, String product);

    List<ViewHistory> getViewHistory(String id);
}
