FROM node:18.18
WORKDIR /gpb/front/react
COPY package.json /gpb/front/react
RUN npm install
COPY . /gpb/front/react
CMD ["npm", "start"]