import com.google.protobuf.ExtensionRegistry;
import com.google.transit.realtime.GtfsRealtime;
import com.google.transit.realtime.GtfsRealtimeNYCT;

import java.io.IOException;
import java.net.URL;


// stations.csv: http://web.mta.info/developers/data/nyct/subway/Stations.csv
public class Runner2 {
    public static void main(String[] args) throws IOException {
        URL url = new URL("http://datamine.mta.info/mta_esi.php?key=4d4a960c9b1c61ad1ed62b5fdfaf7018&feed_id=26");
        ExtensionRegistry registry = ExtensionRegistry.newInstance();
        registry.add(GtfsRealtimeNYCT.nyctFeedHeader);
        registry.add(GtfsRealtimeNYCT.nyctStopTimeUpdate);
        registry.add(GtfsRealtimeNYCT.nyctTripDescriptor);
        GtfsRealtime.FeedMessage feedMessage = GtfsRealtime.FeedMessage.parseFrom( url.openStream(), registry );
        //System.out.println(new Gson().toJson(feedMessage));
        for( GtfsRealtime.FeedEntity entity : feedMessage.getEntityList() )
        {
            if( entity.hasTripUpdate()) {
                //if (entity.getTripUpdate().getTrip().getRouteId().equals("F")) {
                    System.out.println(entity.getTripUpdate().getTrip()); //this just prints out the trip information
                    System.out.println(entity.getTripUpdate().getStopTimeUpdateList());
                    System.out.println("-----------------------------");
               // }
            }

        }

        //Trip.Direction direction = Trip.Direction.fromString("nOrth2");
    }
}
