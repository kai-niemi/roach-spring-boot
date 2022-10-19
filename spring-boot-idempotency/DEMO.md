# Demo

Tutorial of using idempotent POSTs.

## Conditional POST Request

First get a transfer form template:

    curl http://localhost:8090/transfer/form | jq

Next, sign the form by following the `roach-spring:transfer-signature` link rel in the previous response:

    curl -v -d '{"legs":[{"id":1,"amount":10.0},{"id":2,"amount":-10.0},{"id":3,"amount":15.0},{"id":4,"amount":-15.0}]}' -H "Content-Type:application/json" -X GET http://localhost:8090/transfer/signature | jq

In the response you will find a `X-transfer` header which represents a hash of the current state of the accounts 1, 2, 3 and 4:

    X-transfer: 0ed104255363925a54790b0e11eac725a5f66caf4d8d244421c4c53485bb1c85

Next, post the transfer form by following the `roach-spring:transfer` link rel:

    curl -v -d '{"legs":[{"id":1,"amount":10.0},{"id":2,"amount":-10.0},{"id":3,"amount":15.0},{"id":4,"amount":-15.0}]}' -H "Content-Type:application/json" -X POST http://localhost:8090/transfer/signature/0ed104255363925a54790b0e11eac725a5f66caf4d8d244421c4c53485bb1c85 | jq

If all goes well, expect a 201 in return:

    HTTP/1.1 201 Created
    Date: Sun, 16 Oct 2022 07:41:09 GMT
    Content-Type: application/prs.hal-forms+json
    Transfer-Encoding: chunked

The rest of the response contains a resource representation of the transactions created as a result of the fund transfer (the side-effect).

The generated hash `0ed104255363925a54790b0e11eac725a5f66caf4d8d244421c4c53485bb1c85` is now considered consumed and no longer valid, so attempting to re-post the same request will fail with a 412:

    HTTP/1.1 412 Precondition Failed
    Date: Sun, 16 Oct 2022 07:43:16 GMT
    Content-Type: application/problem+json
    Transfer-Encoding: chunked

## Post-Once-Exactly

First get a transfer form template:

    curl http://localhost:8090/transfer/form | jq

Let's follow the `roach-spring:transfer-once` link rel in the previous response:

    curl -v -d '{"legs":[{"id":1,"amount":10.0},{"id":2,"amount":-10.0},{"id":3,"amount":15.0},{"id":4,"amount":-15.0}]}' -H "Content-Type:application/json" -X POST http://localhost:8090/transfer/07f18b6d-9bfd-4a38-af0e-781f21963fcf | jq

If all goes well, expect a 201 in return:

    HTTP/1.1 201 Created
    Date: Sun, 16 Oct 2022 14:58:38 GMT
    POE-Link: 07f18b6d-9bfd-4a38-af0e-781f21963fcf
    Content-Type: application/prs.hal-forms+json
    Transfer-Encoding: chunked

In the response, you will find a `POE-Link` header which represents the UUID token used as idempotency key:

    POE-Link: 07f18b6d-9bfd-4a38-af0e-781f21963fcf

The rest of the response is a resource representation of the transactions created as a result of the transfer (the side-effect). The generated token `07f18b6d-9bfd-4a38-af0e-781f21963fcf` is considered consumed, so attempting to re-post the same request will return a 200 OK to signal deduplication:

    HTTP/1.1 200 OK
    Date: Sun, 16 Oct 2022 15:01:07 GMT
    POE-Link: 07f18b6d-9bfd-4a38-af0e-781f21963fcf
    Content-Type: application/prs.hal-forms+json
    Transfer-Encoding: chunked
