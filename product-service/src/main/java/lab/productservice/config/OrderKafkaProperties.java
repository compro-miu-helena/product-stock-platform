package lab.productservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.kafka")
public class OrderKafkaProperties {

    private String ordersTopic;
    private String ordersErrorHandlerTopic;
    private String ordersErrorHandlerDltTopic;
    private String ordersRetryableTopic;
    private String ordersProducerBatchTopic;
    private String ordersConsumerBatchTopic;
    private String ordersTransactionalTopic;
    private boolean publishSampleOrdersOnStartup = true;
    private final Consumer consumer = new Consumer();

    public String getOrdersTopic() {
        return ordersTopic;
    }

    public void setOrdersTopic(String ordersTopic) {
        this.ordersTopic = ordersTopic;
    }

    public String getOrdersErrorHandlerTopic() {
        return ordersErrorHandlerTopic;
    }

    public void setOrdersErrorHandlerTopic(String ordersErrorHandlerTopic) {
        this.ordersErrorHandlerTopic = ordersErrorHandlerTopic;
    }

    public String getOrdersErrorHandlerDltTopic() {
        return ordersErrorHandlerDltTopic;
    }

    public void setOrdersErrorHandlerDltTopic(String ordersErrorHandlerDltTopic) {
        this.ordersErrorHandlerDltTopic = ordersErrorHandlerDltTopic;
    }

    public String getOrdersRetryableTopic() {
        return ordersRetryableTopic;
    }

    public void setOrdersRetryableTopic(String ordersRetryableTopic) {
        this.ordersRetryableTopic = ordersRetryableTopic;
    }

    public String getOrdersProducerBatchTopic() {
        return ordersProducerBatchTopic;
    }

    public void setOrdersProducerBatchTopic(String ordersProducerBatchTopic) {
        this.ordersProducerBatchTopic = ordersProducerBatchTopic;
    }

    public String getOrdersConsumerBatchTopic() {
        return ordersConsumerBatchTopic;
    }

    public void setOrdersConsumerBatchTopic(String ordersConsumerBatchTopic) {
        this.ordersConsumerBatchTopic = ordersConsumerBatchTopic;
    }

    public String getOrdersTransactionalTopic() {
        return ordersTransactionalTopic;
    }

    public void setOrdersTransactionalTopic(String ordersTransactionalTopic) {
        this.ordersTransactionalTopic = ordersTransactionalTopic;
    }

    public boolean isPublishSampleOrdersOnStartup() {
        return publishSampleOrdersOnStartup;
    }

    public void setPublishSampleOrdersOnStartup(boolean publishSampleOrdersOnStartup) {
        this.publishSampleOrdersOnStartup = publishSampleOrdersOnStartup;
    }

    public Consumer getConsumer() {
        return consumer;
    }

    public static class Consumer {

        private String partitionGroupId;
        private String errorHandlerGroupId;
        private String errorHandlerDltGroupId;
        private String retryableGroupId;
        private String producerBatchGroupId;
        private String consumerBatchGroupId;
        private String transactionalGroupId;

        public String getPartitionGroupId() {
            return partitionGroupId;
        }

        public void setPartitionGroupId(String partitionGroupId) {
            this.partitionGroupId = partitionGroupId;
        }

        public String getErrorHandlerGroupId() {
            return errorHandlerGroupId;
        }

        public void setErrorHandlerGroupId(String errorHandlerGroupId) {
            this.errorHandlerGroupId = errorHandlerGroupId;
        }

        public String getErrorHandlerDltGroupId() {
            return errorHandlerDltGroupId;
        }

        public void setErrorHandlerDltGroupId(String errorHandlerDltGroupId) {
            this.errorHandlerDltGroupId = errorHandlerDltGroupId;
        }

        public String getRetryableGroupId() {
            return retryableGroupId;
        }

        public void setRetryableGroupId(String retryableGroupId) {
            this.retryableGroupId = retryableGroupId;
        }

        public String getProducerBatchGroupId() {
            return producerBatchGroupId;
        }

        public void setProducerBatchGroupId(String producerBatchGroupId) {
            this.producerBatchGroupId = producerBatchGroupId;
        }

        public String getConsumerBatchGroupId() {
            return consumerBatchGroupId;
        }

        public void setConsumerBatchGroupId(String consumerBatchGroupId) {
            this.consumerBatchGroupId = consumerBatchGroupId;
        }

        public String getTransactionalGroupId() {
            return transactionalGroupId;
        }

        public void setTransactionalGroupId(String transactionalGroupId) {
            this.transactionalGroupId = transactionalGroupId;
        }
    }
}
