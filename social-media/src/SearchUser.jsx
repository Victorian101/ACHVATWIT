import "./SearchUser.css";

function SearchUser(props) {
    return (
        <div className="searchBox">
            <div className="searchInputWrap">
                <input
                    className="searchInput"
                    type="text"
                    value={props.filter}
                    onChange={(e) => props.onChange(e.target.value)}
                    placeholder="Search users (min 3 letters)..."
                    autoComplete="off"
                />

                {props.filter?.length > 0 && (
                    <button
                        type="button"
                        className="searchClearBtn"
                        onClick={() => props.onChange("")}
                        aria-label="Clear search"
                        title="Clear"
                    >
                        ✕
                    </button>
                )}
            </div>

            <div className="searchHint">
                {props.filter.trim().length < 3
                    ? "Type at least 3 characters"
                    : props.loading
                        ? "Searching..."
                        : ""}
            </div>
        </div>
    );
}

export default SearchUser;