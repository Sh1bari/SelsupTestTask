package org.example;

import com.google.gson.Gson;
import okhttp3.*;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.*;

public class CrptApi {
    private static final String API_URL = "https://ismp.crpt.ru/api/v3/lk/documents/create";
    private final OkHttpClient client;
    private final Gson gson;
    private final Semaphore semaphore;

    public CrptApi(TimeUnit timeUnit, int requestLimit) {
        this.client = new OkHttpClient();
        this.gson = new Gson();
        this.semaphore = new Semaphore(requestLimit);
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        long delay = Duration.ofMillis(timeUnit.toMillis(1)).toNanos();
        scheduler.scheduleAtFixedRate(semaphore::release, delay, delay, TimeUnit.NANOSECONDS);
    }

    //signature for "Документ и подпись должны передаваться в метод в виде Java объекта и строки соответственно."
    public <T> T createDocument(Document document, String signature, Class<T> responseType) throws InterruptedException, IOException {
        String json = gson.toJson(document);
        RequestBody body = RequestBody.create(json, MediaType.get("application/json"));

        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .addHeader("Signature", signature)
                .build();

        return executeWithSemaphore(request, responseType);
    }

    private <T> T executeWithSemaphore(Request request, Class<T> responseType) throws InterruptedException, IOException {
        semaphore.acquire();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            if (responseType == null) {
                return null;
            }
            String responseBody = response.body() != null ? response.body().string() : null;
            return gson.fromJson(responseBody, responseType);
        } finally {
            semaphore.release();
        }
    }

    //I would prefer to use lombok builder, but I will do the test task manually
    public static class Document {
        private Description description;
        private String doc_id;
        private String doc_status;
        private String doc_type;
        private boolean importRequest;
        private String owner_inn;
        private String participant_inn;
        private String producer_inn;
        private String production_date;
        private String production_type;
        private Product[] products;
        private String reg_date;
        private String reg_number;

        public static class Builder {
            private Description description;
            private String doc_id;
            private String doc_status;
            private String doc_type;
            private boolean importRequest;
            private String owner_inn;
            private String participant_inn;
            private String producer_inn;
            private String production_date;
            private String production_type;
            private Product[] products;
            private String reg_date;
            private String reg_number;

            public Builder description(Description description) {
                this.description = description;
                return this;
            }

            public Builder doc_id(String doc_id) {
                this.doc_id = doc_id;
                return this;
            }

            public Builder doc_status(String doc_status) {
                this.doc_status = doc_status;
                return this;
            }

            public Builder doc_type(String doc_type) {
                this.doc_type = doc_type;
                return this;
            }

            public Builder importRequest(boolean importRequest) {
                this.importRequest = importRequest;
                return this;
            }

            public Builder owner_inn(String owner_inn) {
                this.owner_inn = owner_inn;
                return this;
            }

            public Builder participant_inn(String participant_inn) {
                this.participant_inn = participant_inn;
                return this;
            }

            public Builder producer_inn(String producer_inn) {
                this.producer_inn = producer_inn;
                return this;
            }

            public Builder production_date(String production_date) {
                this.production_date = production_date;
                return this;
            }

            public Builder production_type(String production_type) {
                this.production_type = production_type;
                return this;
            }

            public Builder products(Product[] products) {
                this.products = products;
                return this;
            }

            public Builder reg_date(String reg_date) {
                this.reg_date = reg_date;
                return this;
            }

            public Builder reg_number(String reg_number) {
                this.reg_number = reg_number;
                return this;
            }

            public Document build() {
                Document document = new Document();
                document.description = this.description;
                document.doc_id = this.doc_id;
                document.doc_status = this.doc_status;
                document.doc_type = this.doc_type;
                document.importRequest = this.importRequest;
                document.owner_inn = this.owner_inn;
                document.participant_inn = this.participant_inn;
                document.producer_inn = this.producer_inn;
                document.production_date = this.production_date;
                document.production_type = this.production_type;
                document.products = this.products;
                document.reg_date = this.reg_date;
                document.reg_number = this.reg_number;
                return document;
            }
        }

    }

    public static class Description {
        private String participantInn;

        public static class Builder {
            private String participantInn;

            public Builder participantInn(String participantInn) {
                this.participantInn = participantInn;
                return this;
            }

            public Description build() {
                Description description = new Description();
                description.participantInn = this.participantInn;
                return description;
            }
        }
    }

    public static class Product {
        private String certificate_document;
        private String certificate_document_date;
        private String certificate_document_number;
        private String owner_inn;
        private String producer_inn;
        private String production_date;
        private String tnved_code;
        private String uit_code;
        private String uitu_code;

        public static class Builder {
            private String certificate_document;
            private String certificate_document_date;
            private String certificate_document_number;
            private String owner_inn;
            private String producer_inn;
            private String production_date;
            private String tnved_code;
            private String uit_code;
            private String uitu_code;

            public Builder certificate_document(String certificate_document) {
                this.certificate_document = certificate_document;
                return this;
            }

            public Builder certificate_document_date(String certificate_document_date) {
                this.certificate_document_date = certificate_document_date;
                return this;
            }

            public Builder certificate_document_number(String certificate_document_number) {
                this.certificate_document_number = certificate_document_number;
                return this;
            }

            public Builder owner_inn(String owner_inn) {
                this.owner_inn = owner_inn;
                return this;
            }

            public Builder producer_inn(String producer_inn) {
                this.producer_inn = producer_inn;
                return this;
            }

            public Builder production_date(String production_date) {
                this.production_date = production_date;
                return this;
            }

            public Builder tnved_code(String tnved_code) {
                this.tnved_code = tnved_code;
                return this;
            }

            public Builder uit_code(String uit_code) {
                this.uit_code = uit_code;
                return this;
            }

            public Builder uitu_code(String uitu_code) {
                this.uitu_code = uitu_code;
                return this;
            }

            public Product build() {
                Product product = new Product();
                product.certificate_document = this.certificate_document;
                product.certificate_document_date = this.certificate_document_date;
                product.certificate_document_number = this.certificate_document_number;
                product.owner_inn = this.owner_inn;
                product.producer_inn = this.producer_inn;
                product.production_date = this.production_date;
                product.tnved_code = this.tnved_code;
                product.uit_code = this.uit_code;
                product.uitu_code = this.uitu_code;
                return product;
            }
        }

    }

    public static void main(String[] args) throws InterruptedException, IOException {
        CrptApi api = new CrptApi(TimeUnit.SECONDS, 5);

        Document document = new Document();

        api.createDocument(document, "signature_string", null);

        //future response cast
        //ApiResponse response = api.createDocument(document, "signature_string", ApiResponse.class);
        //System.out.println(response);
        api.createDocument(document, "signature_string", null);
    }
}