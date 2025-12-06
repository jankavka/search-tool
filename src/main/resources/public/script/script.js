document.addEventListener("DOMContentLoaded", () => {
  let searchButton = document.getElementById("search");
  let saveButton = document.getElementById("save");
  let input = document.getElementById("my-input");
  let results = document.getElementById("results");
  let errorMessage = document.getElementById("errorMessage");
  let items;

  /**
   * Method fetches json data from server based on query param
   * @param {*} query used for google search
   * @returns json object
   */
  const fetchData = async (query = "") => {
    response = await fetch(`search?query=${query}`);

    if (!response.ok) {
      let errorResponse = await response.json();
      throw new Error(errorResponse.message);
    }

    return response.json();
  };

  /**
   * Method downolads blob data form server based on query param
   * @param {*} query used for google search
   * @returns blob object
   */
  const downloadData = async (query = "") => {
    let response = await fetch(`search/download?query=${query}`);

    if (!response.ok) {
      let errorResponse = await response.json();
      throw new Error(errorResponse.message);
    }
    console.log(response);

    return response.blob();
  };

  /**
   * Methods adds event listener to an element to get results form 
   * google search call on server, based on query param taken from 
   * input element.
   */
  searchButton.addEventListener("click", () => {
    errorMessage.setAttribute("hidden", "true");
    let child = results.lastElementChild;
    while (child) {
      results.removeChild(child);
      child = results.lastElementChild;
    }
    let query = input.value;

    fetchData(query)
      .then((data) => {
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
    errorMessage.setAttribute("hidden", "true");
    let child = results.lastElementChild;
    while (child) {
      results.removeChild(child);
      child = results.lastElementChild;
    }
    let query = input.value;

    downloadData(query)
      .then((blob) => {
        //creates object URL
        const url = window.URL.createObjectURL(new Blob([blob]));
        //temporary link element
        const link = document.createElement("a");
        //setting URL object as href attribute
        link.href = url;
        //setting download attribute
        link.setAttribute("download", "result.json");
        document.body.appendChild(link);
        //downolad invoked
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
