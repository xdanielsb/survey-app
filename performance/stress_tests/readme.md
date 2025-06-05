# Performance tests for critical routes

### Build:  
   docker build -t stress-test-runner .

### Test local
```sh
docker run --add-host=host.docker.internal:host-gateway \
  -e VUS=1000\
  -e BASE_URL=http://host.docker.internal:8080 \
  -e K6_SCRIPT=get-surveys.js \
stress-test-runner
```

### Test with live test server 
```sh
docker run \
  -e VUS=1000 \
  -e BASE_URL=https://test.app-survey.tech/api/ \
  -e K6_SCRIPT=get-surveys.js \
stress-test-runner
```



Test. Break things. Improve. :v 

