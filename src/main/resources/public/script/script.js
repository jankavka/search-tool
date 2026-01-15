document.addEventListener("DOMContentLoaded", () => {
  let searchButton = document.getElementById("search");
  let saveButton = document.getElementById("save");
  let input = document.getElementById("my-input");
  let results = document.getElementById("results");
  let errorMessage = document.getElementById("errorMessage");
  let lastSearchedQuery;
  let items;

  /**
   * Method fetches json data from server based on query param
   * @param {*} query used for google search
   * @returns json object
   */
  const fetchData = async (query = "") => {
    const response = await fetch(`search?query=${query}`);

    if (!response.ok) {
      let errorResponse = await response.json();
      throw new Error(errorResponse.message);
    }
    
    return response.json();
  };

  /**
   * Method downloads blob data form server based on query param
   * @param {*} query used for google search
   * @returns blob object
   */
  const downloadData = async (query = "") => {
    let response = await fetch(`search/download?query=${query}`);

    if (!response.ok) {
      let errorResponse = await response.json();
      throw new Error(errorResponse.message);
    }

    return response.blob();
  };

  //Removes all child elements form the result element
  const removeElements = () => {
    let child = results.lastElementChild;

    while (child) {
      results.removeChild(child);
      child = results.lastElementChild;
    }
  };

  /**
   * Methods adds event listener to an element to get results form
   * google search call on server, based on query param taken from
   * input element.
   */
  searchButton.addEventListener("click", () => {
    //Sets element with error message hidden
    errorMessage.setAttribute("hidden", "true");

    removeElements();

    let query = input.value;

    //fetches data and sets up child elements
    fetchData(query)
      .then((data) => {
        lastSearchedQuery = query;
        items = data;

        for (let item of items) {
          let element = document.createElement("div");

          let title = document.createElement("h4");
          title.innerText = item.title;

          let link = document.createElement("a");
          link.setAttribute("href", item.link);
          link.innerText = item.link;

          let snippet = document.createElement("p");
          snippet.innerText = item.snippet;

          element.appendChild(title);
          element.appendChild(link);
          element.appendChild(snippet);
          element.setAttribute("class", "mb-5");

          results.appendChild(element);
        }
      })
      .catch((error) => {
        console.error(error.message);

        //Setting up error element
        errorMessage.innerText = error.message;
        errorMessage.removeAttribute("hidden");
      });
  });

  /**
   * Method adds event listener to saveButton element to invoke donwload
   * of json file coming from google search call on server, based
   * on query param taken from input element.
   */
  saveButton.addEventListener("click", () => {
    //Sets element with error message hidden
    errorMessage.setAttribute("hidden", "true");

    let query = input.value;

    if (query !== lastSearchedQuery) {
      removeElements();
    }

    downloadData(query)
      .then((blob) => {
        //creates object URL
        const url = window.URL.createObjectURL(new Blob([blob]));
        //temporary link element
        const link = document.createElement("a");
        //setting URL object as href attribute
        link.href = url;
        //setting download attribute
        link.setAttribute("download", "search-result.json");
        document.body.appendChild(link);
        //download invoked
        link.click();
        //element removed
        document.body.removeChild(link);
        //release existing URL object
        window.URL.revokeObjectURL(url);
      })
      .catch((error) => {
        console.error(error.message);

        errorMessage.innerText = error.message;

        errorMessage.removeAttribute("hidden");
      });
  });
});
