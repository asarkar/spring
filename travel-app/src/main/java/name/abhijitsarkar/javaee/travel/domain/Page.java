package name.abhijitsarkar.javaee.travel.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;

import static java.util.stream.Collectors.toList;

/**
 * @author Abhijit Sarkar
 */
@Data
@AllArgsConstructor
public class Page<T> {
    private int pageNum;
    private int pageSize;
    private int numPages;
    private Collection<T> data;

    public Page() {
        data = new ArrayList<>();
    }

    public <S extends Collection<T>> void setData(S data) {
        this.data = data;
    }

    public void updateFrom(Page<T> from) {
        pageNum = from.pageNum;
        pageSize = from.pageSize;
        numPages = from.numPages;
        data = from.data.stream().filter(e -> e != null).collect(toList());
    }
}
