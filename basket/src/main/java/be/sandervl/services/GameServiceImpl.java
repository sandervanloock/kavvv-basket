package be.sandervl.services;

import be.sandervl.business.Game;
import be.sandervl.business.Ranking;
import be.sandervl.business.checker.GameDateChecker;
import be.sandervl.business.checker.GameTypeChecker;
import be.sandervl.business.comparator.GameComparator;
import be.sandervl.business.util.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class GameServiceImpl implements GameService {

    DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy kk.mm");
    DateFormat formatter2 = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    @Cacheable("gameCache")
    public List<Game> getGames() {
        List<Game> result = new ArrayList<Game>();
        try {
            Map<String, String> postData = initializePostData();
            for (int weekend = 1; weekend <= 37; weekend++) {
                postData.put("ctl00$MainContent$cboWeekend", Integer.toString(weekend));
                result.addAll(getGamesForDate(postData, weekend));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Collections.sort(result, new GameComparator());
        return result;
    }

    @Override
    @Cacheable("gameCache")
    public List<Game> getSpecificGames(Date date, Ranking.Type type) {
        try {
            List<Game> gamesForDate;
            if (date != null) {
                int weekend = getDateValue(date);
                Map<String, String> postData = initializePostData();
                postData.put("ctl00$MainContent$cboWeekend", Integer.toString(weekend));
                gamesForDate = getGamesForDate(postData, weekend);
                gamesForDate = new ArrayList<Game>(CollectionUtils.findAll(gamesForDate, new GameDateChecker(date)));
            } else {
                gamesForDate = getGames();
            }
            if (type != null) {
                gamesForDate = new ArrayList<Game>(CollectionUtils.findAll(gamesForDate, new GameTypeChecker(type)));
            }
            Collections.sort(gamesForDate, new GameComparator());
            return gamesForDate;
        } catch (IOException e) {
//            e.printStackTrace();
        } catch (ParseException e) {
//            e.printStackTrace();
        }
        return new ArrayList<Game>();
    }

    private Map<String, String> initializePostData() throws IOException {
        Document doc = Jsoup.connect("http://www.kavvv-basket.be/kavvv/results.aspx").get();
        String viewState = doc.select("#__VIEWSTATE").attr("value");
        String eventValidation = doc.select("#__EVENTVALIDATION").attr("value");
        String viewStateGenerator = doc.select("#__VIEWSTATEGENERATOR").attr("value");
        Map<String, String> postData = new HashMap<String, String>();
        postData.put("ctl00$MainContent$tsm", "ctl00$MainContent$updPnl|ctl00$MainContent$cboWeekend");
        postData.put("__EVENTTARGET", "ctl00$MainContent$cboWeekend");
        postData.put("__ASYNCPOST", "true");
        postData.put("__EVENTVALIDATION", eventValidation);
        postData.put("__VIEWSTATE", viewState);
        postData.put("__VIEWSTATEGENERATOR", viewStateGenerator);
        return postData;
    }

    private List<Game> getGamesForDate(Map<String, String> postData, int weekend) throws IOException {
        List<Game> result = new ArrayList<Game>();
        Document doc;
        postData.put("ctl00$MainContent$cboWeekend", Integer.toString(weekend));
        doc = Jsoup.connect("http://www.kavvv-basket.be/kavvv/results.aspx")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.71 Safari/537.36")
                .data(postData)
                .post();
        Elements rankingTable = doc.select("table");
        Iterator<Element> rows = rankingTable.select("tr").iterator();
        Ranking.Type rankingType = null;
        while (rows.hasNext()) {
            Element row = rows.next();
            Elements th = row.select("th");
            if (th.size() == 1) {
                String name = th.get(0).html();
                rankingType = Ranking.Type.fromName(name);
            }
            if (rankingType != null) {
                tryParse(row, result, rankingType);
            }
        }
        return result;
    }

    private int getDateValue(Date date) throws IOException, ParseException {
        date = DateUtils.truncate(date, Calendar.DATE);
        Document doc = Jsoup.connect("http://www.kavvv-basket.be/kavvv/results.aspx").get();
        Iterator<Element> options = doc.select("[name=ctl00$MainContent$cboWeekend]").select("option").iterator();
        while (options.hasNext()) {
            Element option = options.next();
            String startDateString = option.html().substring(0, 10);
            String endDateString = option.html().substring(13);
            Date startDate = formatter2.parse(startDateString);
            Date endDate = formatter2.parse(endDateString);
            if (startDate.before(date) && endDate.after(date)) {
                return Integer.parseInt(option.attr("value"));
            }
        }
        return 1;
    }

    private void tryParse(Element row, List<Game> games, Ranking.Type type) {
        Game game = new Game();
        game.setType(type);
        Elements cells = row.select("td");
        if (cells.size() == 8) {
            try {
                String dateWithDay = cells.get(0).select("span").get(0).html();
                String time = cells.get(1).select("span").get(0).html();
                Date date = formatter.parse(dateWithDay.substring(3) + " " + time);
                game.setDate(date);
                String team1 = cells.get(2).select("span").get(0).html();
                game.setTeam1(team1);
                String team2 = cells.get(3).select("span").get(0).html();
                game.setTeam2(team2);
                String score1String = cells.get(4).select("span").get(0).html();
                if (!score1String.isEmpty()) {
                    int score1 = Integer.parseInt(score1String);
                    game.setScore1(score1);
                }
                String score2String = cells.get(6).select("span").get(0).html();
                if (!score2String.isEmpty()) {
                    int score2 = Integer.parseInt(score2String);
                    game.setScore2(score2);
                }
                if (score1String.isEmpty() && score2String.isEmpty()) {
                    game.setFuture(true);
                }
                String canceledString = cells.get(7).select("span").get(0).html();
                if (!canceledString.isEmpty()) {
                    game.setCanceled(true);
                }
                games.add(game);
            } catch (ParseException e) {
//                e.printStackTrace();
            } catch (NumberFormatException e) {
//                e.printStackTrace();
            }
        }
    }
}
