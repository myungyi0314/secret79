package kafkaTest;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.IOException;
import java.util.Properties;


public class KafkaProducerTest {

    private static final String TOPIC_TEST_NAME = "jmpark";  // test_topic_name
    private static final String TOPIC_ES = "mapped_event";  // topic_name

    public static void main(String[] args) throws IOException, InterruptedException {

        Properties configs = new Properties(); // k
        configs.put("bootstrap.servers", "192.168.124.250:1323"); // kafka host 및 server 설정
        configs.put("acks", "all");                         // 자신이 보낸 메시지에 대해 카프카로부터 확인을 기다리지 않습니다.
        configs.put("block.on.buffer.full", "true");        // 서버로 보낼 레코드를 버퍼링 할 때 사용할 수 있는 전체 메모리의 바이트수
        configs.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");   // serialize 설정
        configs.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");    // serialize 설정

        // producer 생성
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(configs);

        // message 전달
        int count = 0;
        Gson gson = new Gson();
        JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty("@index", "jmparktest08");
        jsonobject.addProperty("@index_day", "2022.07");
        jsonobject.addProperty("category", "event");
        jsonobject.addProperty("action", "dfinder-event-forwarder-test");
        jsonobject.addProperty("userid", "interezen");
// Start time
        long startTime = System.currentTimeMillis();
        System.out.println("스타트타임 >>>>>>>>>>>>>>>>>>>>>>>>>"+startTime);
        while (true) {

            jsonobject.addProperty("@id", "c96654bb135e4f51935ff477819bad" + count);
//            System.out.println("JsonObject blog(String) test : "+count + jsonobject.get("@id").getAsString());
//            String json = gson.toJson(jsonobject);
            producer.send(new ProducerRecord<String, String>(TOPIC_ES, jsonobject.toString()));

            if (count > 1) {
                break;
            }
            count++;

//            Thread.sleep(1000); // 1초

        }
        // 종료
        producer.flush();
        // producer 닫기
        producer.close();

        // End time
        long endTime = System.currentTimeMillis();
        System.out.println("앤드타임 >>>>>"+endTime);
        System.out.println("소요시간 >>>>>"+(endTime-startTime));

    }

}
// logstash event가 하는 역할
/*
* 이벤트랑 통신을 해서
* http통신을 한다.
* was
*
*
*
*
* 1 http 붙기
* 2 http request
* 3 event가 응답이 오겠지
* 4
*
* 정상이면 >> 아무 데이터도 없으면 이벤트는 t 라는 값을 준다.
* 비정상이면 >> action 값
*
* http 응답만 받아라
*
* http 규약 깃랩에
* * */