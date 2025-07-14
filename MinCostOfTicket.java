import java.util.HashSet;
import java.util.Set;

/**
 * Leetcode 983. Minimum Cost For Tickets
 * Link: https://leetcode.com/problems/minimum-cost-for-tickets/description/
 */
//------------------------------------ Solution 1 -----------------------------------
public class MinCostOfTicket {
    /**
     * Brute force solution: Top to bottom recursive solution where we choose each of the passes
     * and recursively calculate new cost post the expiry of current pass. i.e. 1, 7, 30 days after for
     * respective 1,7,30 day passes. It is possible that at future date there is no travel required so
     * we can increment the currday to next day until we find a day for which travel is required.
     *
     * TC: O(3^n) 3 because we have 3 choices in pass and n is length of days array
     * Auxiliary SC: O(1) actually it is k <= 365 days for the daySet which is considered constant
     * Recursive stack SC: O(n) number of days in the array. Since it is linear and always <= 365,
     *                      it could be considered constant too
     */
    public int mincostTickets(int[] days, int[] costs) {
        Set<Integer> daySet = new HashSet<>();

        for(int day: days) {
            daySet.add(day);
        }
        return helper(days, costs, daySet, 0);
    }

    private int helper(int[] days, int[] costs, Set<Integer> daySet, int currDay) {
        //base
        if (currDay > days[days.length - 1]) {
            return 0;
        }

        //logic
        if (!daySet.contains(currDay)) {
            return helper(days, costs, daySet, currDay + 1);
        }

        int oneDayPass = costs[0] + helper(days, costs, daySet, currDay + 1);
        int sevenDayPass = costs[1] + helper(days, costs, daySet, currDay + 7);
        int oneMonthPass = costs[2] + helper(days, costs, daySet, currDay + 30);

        return Math.min(oneDayPass, Math.min(sevenDayPass, oneMonthPass));
    }
}

//------------------------------------ Solution 2 -----------------------------------
class MinCostOfTicket2 {
    /**
     * Optimization for the above solution using top down memoization where we will have
     * 1D memo array to store any calculated minimum cost of a given currentDay and reuse the same value
     * for the unwinding recursive calls.
     *
     * TC: O(1) actually it is k <= 365 days. hence considered constant
     * Auxiliary SC: O(1) max length of DP array and daySet which is k <= 365 days. hence considered constant
     * Recursive stack SC: O(n) number of days in the array. Since it is linear and always <= 365,
     *                      it could be considered constant too
     */
    int[] memo;
    public int mincostTickets(int[] days, int[] costs) {
        int last = days[days.length - 1];
        int first = days[0];
        this.memo = new int[last + 1];
        Set<Integer> daySet = new HashSet<>();

        for(int day: days) {
            daySet.add(day);
        }
        return helper(days, costs, daySet, first);
    }

    private int helper(int[] days, int[] costs, Set<Integer> daySet, int currDay) {
        //base
        if (currDay > days[days.length - 1]) {
            return 0;
        }

        //logic
        if (!daySet.contains(currDay)) {
            return helper(days, costs, daySet, currDay + 1);
        }
        if (memo[currDay] != 0) {
            return memo[currDay];
        }

        int oneDayPass = costs[0] + helper(days, costs, daySet, currDay + 1);
        int sevenDayPass = costs[1] + helper(days, costs, daySet, currDay + 7);
        int oneMonthPass = costs[2] + helper(days, costs, daySet, currDay + 30);

        memo[currDay] =  Math.min(oneDayPass, Math.min(sevenDayPass, oneMonthPass));
        return memo[currDay];
    }
}

//------------------------------------ Solution 3 -----------------------------------
class MinCostOfTicket3 {
    /**
     * Bottom up DP solution with 1D tabulation: idea is to start from the first day in the days array
     * and find the minimum cost considering all 3 cost options along with their counterpart dp index by
     * those many days back . In DP 1D array we will maintain entries for all 365 days (or from 0 to last
     * day in the days array) where each index in dp represent a day in the days array. if for a given dp index
     * corresponding day isn't present in the days array we can just copy previous index DP value to current index.
     * This ensures that when we try to find a counterpart dp index by those many days back we have an entry in DP
     *
     * TC: O(1) actually it is k <= 365 days. hence considered constant
     * SC: O(1) max length of DP array which is k <= 365 days. hence considered constant
     */
    public int mincostTickets(int[] days, int[] costs) {
        int first = days[0];
        int last = days[days.length - 1];
        int[] dp = new int[last + 1];

        int day = 0; // 1st entry/day at index 0 in days array
        for (int i = first; i < dp.length; i++) {
            if (i < days[day]) { //no entry for day = i index in days array so copy previous dp value
                dp[i] = dp[i - 1];
            } else { // i will always be equal to days[day]
                int prev1 = dp[i - 1]; //1day pass
                int prev7 = 0, prev30 = 0;
                if (i  - 7 >= 0) { //7day pass
                    prev7 = dp[i - 7];
                }
                if (i  - 30 >= 0) { //30day pass
                    prev30 = dp[i - 30];
                }
                dp[i] = Math.min(prev1 + costs[0], Math.min(prev7 + costs[1], prev30 + costs[2]));
                day++; //next entry in the days array
            }
        }

        return dp[last];
    }
}