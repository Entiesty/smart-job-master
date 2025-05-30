export const convertFile2Base64 = (file) => {
    return new Promise((resolve, reject) => {
      let img = new FileReader()
      img.readAsDataURL(file)
      img.onload = ({ target }) => {
        resolve(target.result)
      }
    })
  }