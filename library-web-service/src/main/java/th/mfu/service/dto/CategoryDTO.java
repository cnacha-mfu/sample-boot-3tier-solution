package th.mfu.service.dto;

/**
 * What a category looks like on the wire.
 *
 * The entity has a List&lt;Book&gt;. This does not. That is a decision, not an
 * oversight: a client asking for the list of categories does not want every
 * book in every one of them. Books are fetched separately, from
 * GET /categories/{id}/books.
 */
public class CategoryDTO {

    private Long id;
    private String name;
    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
