package com.woragis.campusworld.api.dto;

public class RollbackResponse {
    private RollbackData rollback;

    public RollbackData getRollback() {
        return rollback;
    }

    public static class RollbackData {
        private String id;
        private int itemCount;
        private String status;

        public String getId() {
            return id;
        }

        public int getItemCount() {
            return itemCount;
        }

        public String getStatus() {
            return status;
        }
    }
}
