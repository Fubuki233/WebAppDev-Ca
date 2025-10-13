package sg.com.aori.service;

/**
 * HistoryService implements IViewHistory to manage product view history.
 * @Author Yunhe
 * @date 2025-10-12
 * @version 1.0
 */
import sg.com.aori.interfaces.IViewHistory;

import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sg.com.aori.repository.ViewHistoryRepository;
import sg.com.aori.model.ViewHistory;

@Service
public class HistoryService implements IViewHistory {
    @Autowired
    private ViewHistoryRepository viewHistoryRepository;

    @Override
    public String addView(String userId, String productId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userId", userId);
        jsonObject.put("productId", productId);

        List<ViewHistory> existingViews = viewHistoryRepository.findByUserIdAndProductId(userId, productId);

        if (!existingViews.isEmpty()) {
            ViewHistory viewHistory = existingViews.get(0);
            viewHistory.setTimestamp(System.currentTimeMillis());
            viewHistoryRepository.save(viewHistory);

            if (existingViews.size() > 1) {
                for (int i = 1; i < existingViews.size(); i++) {
                    viewHistoryRepository.delete(existingViews.get(i));
                }

            }

            jsonObject.put("action", "updated");
            jsonObject.put("timestamp", viewHistory.getTimestamp());
            jsonObject.put("id", viewHistory.getId());
        } else {
            ViewHistory newView = new ViewHistory(userId, productId);
            viewHistoryRepository.save(newView);

            jsonObject.put("action", "created");
            jsonObject.put("timestamp", newView.getTimestamp());
            jsonObject.put("id", newView.getId());
        }

        return jsonObject.toString();
    }

    @Override
    public List<ViewHistory> getViewHistory(String userId) {
        return viewHistoryRepository.findByUserIdOrderByTimestampDesc(userId);
    }

}
