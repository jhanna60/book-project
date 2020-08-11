package com.karankumar.bookproject.backend.goal;

import com.karankumar.bookproject.backend.entity.Book;
import com.karankumar.bookproject.backend.entity.PredefinedShelf;
import com.karankumar.bookproject.backend.entity.ReadingGoal;
import com.karankumar.bookproject.backend.utils.DateUtils;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public class CalculateReadingGoal {

    private static final String BEHIND = "behind";
    private static final String AHEAD_OF = "ahead of";

    private CalculateReadingGoal() {}

    /**
     * Calculates the number of books that should have been read by this point in the year in order to be on target to
     * achieving the goal
     * @param booksToReadThisYear the number of books to read by the end of the year (the goal)
     * @return the number of books that the user should have read by this point in the year
     */
    public static int shouldHaveRead(int booksToReadThisYear) {
        return booksToReadFromStartOfYear(booksToReadThisYear) * DateUtils.getWeekOfYear();
    }

    /**
     * @param booksToReadThisYear the number of books to read by the end of the year (the goal)
     * @return the number of books that should have been read a week (on average) from the start of the year
     */
    public static int booksToReadFromStartOfYear(int booksToReadThisYear) {
        return ((int) Math.ceil(booksToReadThisYear / DateUtils.getWeeksInYear()));
    }

    /**
     * Find how many books or pages have been read this year
     * @param goalType either books or pages
     * @param readShelf the predefined read shelf
     * @return the number of books or pages read this year
     */
    public static int howManyReadThisYear(ReadingGoal.GoalType goalType, @NotNull PredefinedShelf readShelf) {
        int readThisYear = 0;
        boolean lookingForBooks = goalType.equals(ReadingGoal.GoalType.BOOKS);
        for (Book book : readShelf.getBooks()) {
            // only books that have been given a finish date can count towards the reading goal
            if (book != null && book.getDateFinishedReading() != null
                    && book.getDateFinishedReading().getYear() == LocalDate.now().getYear()) {
                int pages = (book.getNumberOfPages() == null) ? 0 : book.getNumberOfPages();
                readThisYear += (lookingForBooks ? (1) : pages);
            }
        }
        return readThisYear;
    }

    public static int howFarAheadOrBehindSchedule(int booksToReadThisYear, int booksReadThisYear) {
        int shouldHaveRead = booksToReadFromStartOfYear(booksToReadThisYear) * DateUtils.getWeekOfYear();
        return Math.abs(shouldHaveRead - booksReadThisYear);
    }

    /**
     * Calculates a user's progress towards their reading goal
     * @param toRead the number of books to read by the end of the year (the goal)
     * @param read the number of books that the user has read so far
     * @return a fraction of the number of books to read over the books read. If greater than 1, 1.0 is returned
     */
    public static double calculateProgressTowardsReadingGoal(int toRead, int read) {
        double progress = (toRead == 0) ? 0 : ((double) read / toRead);
        return Math.min(progress, 1.0);
    }

    /**
     * Note that this method assumes that the user is behind or ahead of schedule (and that they haven't met their goal)
     * @param booksReadThisYear the number of books read so far
     * @param shouldHaveRead the number of books that should have been ready by this point to be on schedule
     * @return a String denoting that the user is ahead or behind schedule
     */
    public static String behindOrAheadSchedule(int booksReadThisYear, int shouldHaveRead) {
        return (booksReadThisYear < shouldHaveRead) ? BEHIND : AHEAD_OF;
    }
}
