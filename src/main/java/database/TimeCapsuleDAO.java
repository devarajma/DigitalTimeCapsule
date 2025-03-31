package database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.FindIterable;
import models.TimeCapsule;
import org.bson.Document;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TimeCapsuleDAO {
    private final MongoCollection<Document> collection;

    public TimeCapsuleDAO() {
        this.collection = MongoDBConnection.getDatabase().getCollection("capsules");
    }

    public void saveCapsule(TimeCapsule capsule) {
        collection.insertOne(capsule.toDocument());
    }

    public List<TimeCapsule> getReadyToOpenCapsules() {
        List<TimeCapsule> capsules = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (Document doc : collection.find()) {
            TimeCapsule capsule = TimeCapsule.fromDocument(doc);
            if (capsule != null && capsule.getOpenDateTime().isBefore(now)) {
                capsules.add(capsule);
            }
        }
        return capsules;
    }

}
