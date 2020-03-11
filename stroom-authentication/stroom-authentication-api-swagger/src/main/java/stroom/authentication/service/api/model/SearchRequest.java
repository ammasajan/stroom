/*
 * Stroom Auth API
 * Various APIs for interacting with authentication, users, and tokens.
 *
 * OpenAPI spec version: v1
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */


package stroom.authentication.service.api.model;

import java.util.Objects;
import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.HashMap;
import java.util.Map;

/**
 * A request for a search over tokens.
 */
@ApiModel(description = "A request for a search over tokens.")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2019-11-26T10:17:08.903Z")
public class SearchRequest {
  @SerializedName("page")
  private Integer page = null;

  @SerializedName("limit")
  private Integer limit = null;

  @SerializedName("orderBy")
  private String orderBy = null;

  @SerializedName("orderDirection")
  private String orderDirection = null;

  @SerializedName("filters")
  private Map<String, String> filters = new HashMap<String, String>();

  public SearchRequest page(Integer page) {
    this.page = page;
    return this;
  }

   /**
   * The page of search results to retrieve.
   * @return page
  **/
  @ApiModelProperty(example = "null", required = true, value = "The page of search results to retrieve.")
  public Integer getPage() {
    return page;
  }

  public void setPage(Integer page) {
    this.page = page;
  }

  public SearchRequest limit(Integer limit) {
    this.limit = limit;
    return this;
  }

   /**
   * The number of tokens in a page of search results.
   * @return limit
  **/
  @ApiModelProperty(example = "null", required = true, value = "The number of tokens in a page of search results.")
  public Integer getLimit() {
    return limit;
  }

  public void setLimit(Integer limit) {
    this.limit = limit;
  }

  public SearchRequest orderBy(String orderBy) {
    this.orderBy = orderBy;
    return this;
  }

   /**
   * The property by which to order the results.
   * @return orderBy
  **/
  @ApiModelProperty(example = "null", value = "The property by which to order the results.")
  public String getOrderBy() {
    return orderBy;
  }

  public void setOrderBy(String orderBy) {
    this.orderBy = orderBy;
  }

  public SearchRequest orderDirection(String orderDirection) {
    this.orderDirection = orderDirection;
    return this;
  }

   /**
   * The direction in which to order the results.
   * @return orderDirection
  **/
  @ApiModelProperty(example = "null", value = "The direction in which to order the results.")
  public String getOrderDirection() {
    return orderDirection;
  }

  public void setOrderDirection(String orderDirection) {
    this.orderDirection = orderDirection;
  }

  public SearchRequest filters(Map<String, String> filters) {
    this.filters = filters;
    return this;
  }

  public SearchRequest putFiltersItem(String key, String filtersItem) {
    this.filters.put(key, filtersItem);
    return this;
  }

   /**
   * How to filter the results. This is done by property, e.g. user_email, 'someone@someplace.com'.
   * @return filters
  **/
  @ApiModelProperty(example = "null", value = "How to filter the results. This is done by property, e.g. user_email, 'someone@someplace.com'.")
  public Map<String, String> getFilters() {
    return filters;
  }

  public void setFilters(Map<String, String> filters) {
    this.filters = filters;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SearchRequest searchRequest = (SearchRequest) o;
    return Objects.equals(this.page, searchRequest.page) &&
        Objects.equals(this.limit, searchRequest.limit) &&
        Objects.equals(this.orderBy, searchRequest.orderBy) &&
        Objects.equals(this.orderDirection, searchRequest.orderDirection) &&
        Objects.equals(this.filters, searchRequest.filters);
  }

  @Override
  public int hashCode() {
    return Objects.hash(page, limit, orderBy, orderDirection, filters);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SearchRequest {\n");
    
    sb.append("    page: ").append(toIndentedString(page)).append("\n");
    sb.append("    limit: ").append(toIndentedString(limit)).append("\n");
    sb.append("    orderBy: ").append(toIndentedString(orderBy)).append("\n");
    sb.append("    orderDirection: ").append(toIndentedString(orderDirection)).append("\n");
    sb.append("    filters: ").append(toIndentedString(filters)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
  
}
