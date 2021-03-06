package be.sandervl.webservice;

import be.sandervl.business.Ranking;
import be.sandervl.services.RankingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ranking")
public class RankingController {

    @Autowired
    private RankingService rankingService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<Ranking> getAllRankings() {
        return rankingService.getRankings();
    }
}
