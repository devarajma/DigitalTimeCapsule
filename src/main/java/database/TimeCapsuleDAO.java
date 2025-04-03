//
//package database;
//
//import com.mongodb.client.MongoCollection;
//import com.mongodb.client.FindIterable;
//import models.TimeCapsule;
//import org.bson.Document;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//public class TimeCapsuleDAO {
//    private final MongoCollection<Document> collection;
//
//    public TimeCapsuleDAO() {
//        this.collection = MongoDBConnection.getDatabase().getCollection("capsules");
//    }
//
//    public void saveCapsule(TimeCapsule capsule) {
//        collection.insertOne(capsule.toDocument());
//    }
//
//    public List<TimeCapsule> getReadyToOpenCapsules() {
//        List<TimeCapsule> capsules = new ArrayList<>();
//        LocalDateTime now = LocalDateTime.now();
//
//        for (Document doc : collection.find()) {
//            TimeCapsule capsule = TimeCapsule.fromDocument(doc);
//            if (capsule != null && capsule.getOpenDateTime().isBefore(now)) {
//                capsules.add(capsule);
//            }
//        }
//        return capsules;
//    }
//
//}
//

//Deepseek version
package database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import models.TimeCapsule;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TimeCapsuleDAO {
    private final MongoCollection<Document> collection;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public TimeCapsuleDAO() {
        this.collection = MongoDBConnection.getDatabase().getCollection("capsules");
    }

    public void saveCapsule(TimeCapsule capsule) {
        Document doc = new Document()
                .append("userName", capsule.getUserName())
                .append("message", capsule.getMessage())
                .append("openDateTime", capsule.getOpenDateTime().format(FORMATTER))
                .append("isOpened", false);
        collection.insertOne(doc);
    }

    public List<TimeCapsule> getReadyToOpenCapsules() {
        String now = LocalDateTime.now().format(FORMATTER);
        Bson query = Filters.and(
                Filters.lte("openDateTime", now),
                Filters.eq("isOpened", false)
        );

        return collection.find(query)
                .map(TimeCapsule::fromDocument)
                .into(new ArrayList<>())
                .stream()
                .filter(c -> c != null)
                .collect(Collectors.toList());
    }

    public List<TimeCapsule> getAllCapsules() {
        return collection.find()
                .map(TimeCapsule::fromDocument)
                .into(new ArrayList<>())
                .stream()
                .filter(c -> c != null)
                .collect(Collectors.toList());
    }

    public void markAsOpened(ObjectId id) {
        collection.updateOne(
                Filters.eq("_id", id),
                Updates.set("isOpened", true)
        );
    }
}