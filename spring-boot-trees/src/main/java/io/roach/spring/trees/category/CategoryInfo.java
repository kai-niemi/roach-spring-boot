package io.roach.spring.trees.category;

public class CategoryInfo {
    private final Long id;

    private final String name;

    private final Integer count;

    public CategoryInfo(Long id, String name, Integer count) {
        this.id = id;
        this.name = name;
        this.count = count;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getCount() {
        return count;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("CategoryInfo");
        sb.append("{id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", count=").append(count);
        sb.append('}');
        return sb.toString();
    }
}
